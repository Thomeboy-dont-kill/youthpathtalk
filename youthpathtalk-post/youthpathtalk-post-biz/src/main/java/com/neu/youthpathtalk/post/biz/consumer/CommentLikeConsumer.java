package com.neu.youthpathtalk.post.biz.consumer;

import com.google.common.util.concurrent.RateLimiter;
import com.neu.youthpathtalk.constant.redis.CommentRedisKey;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.dto.CommentMetaDTO;
import com.neu.youthpathtalk.post.biz.enums.NotificationType;
import com.neu.youthpathtalk.post.biz.enums.TargetType;
import com.neu.youthpathtalk.post.biz.event.CommentLikeEvent;
import com.neu.youthpathtalk.post.biz.mapper.CommentLikeRecordMapper;
import com.neu.youthpathtalk.post.biz.mapper.CommentMapper;
import com.neu.youthpathtalk.post.biz.message.NotificationMessage;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/06/10 16:19
 * @description
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_COMMENT_LIKE_EVENT,
        consumerGroup = MQConstants.CONSUMER_GROUP_COMMENT_LIKE,
        consumeMode = ConsumeMode.ORDERLY
)
public class CommentLikeConsumer implements RocketMQListener<CommentLikeEvent> {

    private final CommentLikeRecordMapper commentLikeRecordMapper;
    private final CommentMapper commentMapper;
    private final RateLimiter rateLimiter;
    private final RedisService redisService;
    private final RocketMQTemplate rocketMQTemplate;
    private final JsonUtils jsonUtils;

    public CommentLikeConsumer(
            CommentLikeRecordMapper commentLikeRecordMapper,
            CommentMapper commentMapper,
            @Value("${comment.consumer.rate.likeEvent:5000}") int ratePerSecond,
            RedisService redisService,
            RocketMQTemplate rocketMQTemplate,
            JsonUtils jsonUtils
    ) {
        this.commentLikeRecordMapper = commentLikeRecordMapper;
        this.commentMapper = commentMapper;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
        this.redisService=redisService;
        this.rocketMQTemplate = rocketMQTemplate;
        this.jsonUtils=jsonUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(CommentLikeEvent event) {

        if (!rateLimiter.tryAcquire()) {
            log.warn("【评论点赞消费者】限流触发，消息将重试: {}", event);
            throw new RuntimeException("Rate limit exceeded");
        }

        if (event == null || event.userId() == null || event.commentId() == null) {
            log.warn("无效评论点赞事件: {}", event);
            return;
        }

        Long userId = event.userId();
        Long commentId = event.commentId();
        Boolean interacted = event.interacted();

        try {

            if (Boolean.TRUE.equals(interacted)) {

                int inserted = commentLikeRecordMapper.insertIgnore(userId, commentId);

                if (inserted > 0) {
                    commentMapper.updateLikeCountById(commentId, 1L);
                    sendCommentLikeNotification(event);
                }

            } else {

                int deleted = commentLikeRecordMapper.deleteByUserIdAndCommentId(userId, commentId);

                if (deleted > 0) {
                    commentMapper.updateLikeCountById(commentId, -1L);
                }
            }

        } catch (Exception e) {
            log.error("【评论点赞消费者】处理失败: {}", event, e);
            throw new RuntimeException(e);
        }
    }
    private void sendCommentLikeNotification(CommentLikeEvent event) {
        Long commentId=event.commentId();
        CommentMetaDTO meta = getCommentMeta(commentId);
        if (meta==null){
            log.warn("评论元信息为空，跳过通知, commentId={}", commentId);
            return;
        }
        Long receiverId =  meta.getUserId();
        if (receiverId == null) {
            log.warn("评论作者为空，跳过通知, commentId={}", commentId);
            return;
        }
        Long senderId=event.userId();

        if (Objects.equals(receiverId, senderId)) {
            return;
        }

        NotificationMessage notification =
                NotificationMessage.builder()
                        .eventId(UUID.randomUUID().toString())
                        .receiverId(receiverId)
                        .senderId(senderId)
                        .type(NotificationType.COMMENT_LIKE.getCode())
                        .targetType(TargetType.COMMENT.getCode())
                        .postId(meta.getPostId())
                        .commentId(commentId)
                        .createTime(LocalDateTime.now())
                        .build();

        rocketMQTemplate.asyncSend(
                MQConstants.TOPIC_NOTIFICATION,
                notification,
                new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {

                        log.debug(
                                "评论点赞通知发送成功, commentId={}, receiverId={}",
                                commentId,
                                receiverId
                        );
                    }

                    @Override
                    public void onException(Throwable e) {

                        log.error(
                                "评论点赞通知发送失败, commentId={}, receiverId={}",
                                commentId,
                                receiverId,
                                e
                        );
                    }
                }
        );
    }
    private CommentMetaDTO getCommentMeta(Long commentId){
        if (commentId==null) return null;
        String key= CommentRedisKey.meta(commentId);
        try {
            String cacheResult= redisService.get(key);
            if (cacheResult!=null){
                if (cacheResult.equals(RedisConstants.NULL_PLACEHOLDER)){
                    return null;
                }
                return jsonUtils.parseObject(cacheResult,CommentMetaDTO.class);
            }
        } catch (Exception e) {
            log.error("Redis获取评论元信息缓存失败,commentId={}",commentId,e);
        }
        //缓存未命中或异常，查询数据库
        CommentMetaDTO dbResult=commentMapper.selectMetaById(commentId);
        try {
            if (Objects.isNull(dbResult)){
                //缓存空值，防止穿透
                redisService.set(key,RedisConstants.NULL_PLACEHOLDER,
                        RedisConstants.NULL_VALUE_TTL,RedisConstants.NULL_VALUE_TTL_UNIT);
            }else {
                redisService.setJson(key,dbResult,
                        CommentRedisKey.COMMENT_META_TTL_HOURS, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            log.error("写入评论元信息缓存失败,commentId={}",commentId,e);
        }
        return dbResult;
    }
}
