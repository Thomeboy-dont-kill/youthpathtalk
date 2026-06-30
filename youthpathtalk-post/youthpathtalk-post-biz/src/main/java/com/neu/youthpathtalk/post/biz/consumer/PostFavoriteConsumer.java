package com.neu.youthpathtalk.post.biz.consumer;

import com.google.common.util.concurrent.RateLimiter;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.enums.NotificationType;
import com.neu.youthpathtalk.post.biz.enums.TargetType;
import com.neu.youthpathtalk.post.biz.event.PostFavoriteEvent;
import com.neu.youthpathtalk.post.biz.mapper.FavoriteRecordMapper;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import com.neu.youthpathtalk.post.biz.message.NotificationMessage;
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

/**
 * @author Julien
 * @time 2026/05/28 22:36
 * @description
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_POST_FAVORITE_EVENT,
        consumerGroup = MQConstants.CONSUMER_GROUP_POST_FAVORITE,
        consumeMode = ConsumeMode.ORDERLY
)
public class PostFavoriteConsumer implements RocketMQListener<PostFavoriteEvent> {

    private final FavoriteRecordMapper favoriteRecordMapper;
    private final PostMapper postMapper;
    private final RateLimiter rateLimiter;
    private final RedisService redisService;
    private final RocketMQTemplate rocketMQTemplate;

    public PostFavoriteConsumer(
            FavoriteRecordMapper favoriteRecordMapper,
            PostMapper postMapper,
            @Value("${post.consumer.rate.favoriteEvent:5000}") int ratePerSecond,
            RedisService redisService,
            RocketMQTemplate rocketMQTemplate
    ) {
        this.favoriteRecordMapper = favoriteRecordMapper;
        this.postMapper = postMapper;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
        this.redisService=redisService;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(PostFavoriteEvent favoriteEvent) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("【帖子收藏记录消费者】达到限流阈值，消息将被重试: {}", favoriteEvent);
            throw new RuntimeException("Rate limit exceeded, message will be retried");
        }

        if (favoriteEvent == null || favoriteEvent.getUserId() == null || favoriteEvent.getPostId() == null) {
            log.warn("无效收藏事件: {}", favoriteEvent);
            return;
        }

        Long userId = favoriteEvent.getUserId();
        Long postId = favoriteEvent.getPostId();
        boolean favorited = favoriteEvent.getFavorited();

        try {

            if (favorited) {
                int inserted = favoriteRecordMapper.insertIgnore(userId, postId);

                if (inserted > 0) {
                    postMapper.updateFavoriteCountById(postId, 1L);
                    sendPostFavoriteNotification(favoriteEvent);
                }

            } else {
                int deleted = favoriteRecordMapper.deleteByUserIdAndPostId(userId, postId);

                if (deleted > 0) {
                    postMapper.updateFavoriteCountById(postId, -1L);
                }
            }

        } catch (Exception e) {
            log.error("【帖子】处理收藏事件失败: {}", favoriteEvent, e);
            throw new RuntimeException(e);
        }
    }
    private void sendPostFavoriteNotification(PostFavoriteEvent event) {
        Long postId=event.getPostId();

        Long receiverId = getPostAuthorId(postId);
        if (receiverId == null) {
            log.warn("帖子作者为空，跳过通知, postId={}", postId);
            return;
        }
        Long senderId=event.getUserId();

        if (Objects.equals(receiverId, senderId)) {
            return;
        }

        NotificationMessage notification =
                NotificationMessage.builder()
                        .eventId(UUID.randomUUID().toString())
                        .receiverId(receiverId)
                        .senderId(senderId)
                        .type(NotificationType.POST_FAVORITE.getCode())
                        .targetType(TargetType.POST.getCode())
                        .postId(postId)
                        .createTime(LocalDateTime.now())
                        .build();

        rocketMQTemplate.asyncSend(
                MQConstants.TOPIC_NOTIFICATION,
                notification,
                new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {

                        log.debug(
                                "帖子收藏通知发送成功, postId={}, receiverId={}",
                                postId,
                                receiverId
                        );
                    }

                    @Override
                    public void onException(Throwable e) {

                        log.error(
                                "帖子收藏通知发送失败, postId={}, receiverId={}",
                                postId,
                                receiverId,
                                e
                        );
                    }
                }
        );
    }
    private Long getPostAuthorId(Long postId){
        if (postId==null) return null;
        String authorKey=PostRedisKey.author(postId);
        try {
            String cached= redisService.get(authorKey);
            if (cached!=null){
                if (cached.equals(RedisConstants.NULL_PLACEHOLDER)){
                    return null;
                }
                return Long.parseLong(cached);
            }
        } catch (NumberFormatException e) {
            log.error("Redis获取帖子作者缓存失败,postId={}",postId,e);
        }
        //缓存未命中或异常，查询数据库
        Long authorId=postMapper.selectAuthorIdById(postId);
        try {
            if (Objects.isNull(authorId)){
                //缓存空值，防止穿透
                redisService.set(authorKey,RedisConstants.NULL_PLACEHOLDER,
                        RedisConstants.NULL_VALUE_TTL,RedisConstants.NULL_VALUE_TTL_UNIT);
            }else {
                redisService.set(authorKey,String.valueOf(authorId),
                        PostRedisKey.POST_AUTHOR_TTL,PostRedisKey.POST_AUTHOR_TTL_UNIT);
            }
        } catch (Exception e) {
            log.error("写入帖子作者缓存失败,postId={}",postId,e);
        }
        return authorId;
    }
}
