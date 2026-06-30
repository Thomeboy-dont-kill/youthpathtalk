package com.neu.youthpathtalk.post.biz.consumer;

import com.google.common.util.concurrent.RateLimiter;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.enums.NotificationType;
import com.neu.youthpathtalk.post.biz.enums.TargetType;
import com.neu.youthpathtalk.post.biz.event.PostLikeEvent;
import com.neu.youthpathtalk.post.biz.mapper.PostLikeRecordMapper;
import com.neu.youthpathtalk.post.biz.message.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Julien
 * @time 2026/03/24 9:33
 * @description 点赞关系落库
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_POST_LIKE_RECORD,
        consumerGroup = MQConstants.CONSUMER_GROUP_POST_INTERACTION_RECORD,
        consumeMode = ConsumeMode.ORDERLY
)
public class PostLikeRecordConsumer implements RocketMQListener<PostLikeEvent> {
    private final PostLikeRecordMapper postLikeRecordMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final RateLimiter rateLimiter;

    public PostLikeRecordConsumer(
            PostLikeRecordMapper postLikeRecordMapper,
            RocketMQTemplate rocketMQTemplate,
            @Value("${post.consumer.rate.record.like:5000}") int ratePerSecond
    ) {
        this.postLikeRecordMapper = postLikeRecordMapper;
        this.rocketMQTemplate = rocketMQTemplate;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
    }

    @Override
    public void onMessage(PostLikeEvent likeEvent) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("【帖子点赞记录消费者】达到限流阈值，消息将被重试: {}", likeEvent);
            throw new RuntimeException("Rate limit exceeded, message will be retried");
        }
        if (invalidInteractMessage(likeEvent)){
            log.warn("【帖子】无效的点赞事件:{}",likeEvent);
            return;
        }
        Long userId= likeEvent.getUserId();
        Long postId= likeEvent.getPostId();
        boolean liked=likeEvent.getLiked();
        try {
            if (liked){
                int inserted= postLikeRecordMapper.insertIgnore(userId,postId);
                if (inserted>0){
                    log.debug("【帖子】点赞记录入库成功: userId={}, postId={}", userId, postId);
                    sendPostLikeNotification(likeEvent);
                    Message<PostLikeEvent> message= MessageBuilder.withPayload(likeEvent).build();
                    String destination=MQConstants.TOPIC_POST_LIKE_COLLECT;
                    rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.debug("==> 【聚合: 帖子点赞】RocketMQ发送成功: userId={}, postId={}, liked=true", userId, postId);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("==> 【聚合: 帖子点赞】RocketMQ发送失败: userId={}, postId={}, liked=true", userId, postId, e);
                        }
                    });
                }else {
                    log.debug("【帖子】重复点赞消息忽略: userId={}, postId={}", userId, postId);
                }
            }else {
                int deleted = postLikeRecordMapper.deleteByUserIdAndPostId(userId,postId);
                if (deleted > 0) {
                    log.debug("【帖子】取消点赞记录入库成功: userId={}, postId={}", userId, postId);
                    Message<PostLikeEvent> message= MessageBuilder.withPayload(likeEvent).build();
                    String destination=MQConstants.TOPIC_POST_LIKE_COLLECT;
                    String hashKey=String.valueOf(postId);
                    rocketMQTemplate.asyncSendOrderly(destination, message, hashKey,new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.debug("==> 【聚合: 帖子点赞】RocketMQ发送成功: userId={}, postId={}, liked=false", userId, postId);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("==> 【聚合: 帖子点赞】RocketMQ发送失败: userId={}, postId={}, liked=false", userId, postId, e);
                        }
                    });
                }else {
                    log.debug("【帖子】重复取消点赞消息忽略: userId={}, postId={}", userId, postId);
                }
            }
        } catch (Exception e) {
            log.error("【帖子】处理点赞消息失败: event={}", likeEvent, e);
            throw new RuntimeException("处理点赞消息失败", e);
        }
    }
    private void sendPostLikeNotification(PostLikeEvent event) {

        Long receiverId = event.getAuthorId();
        if (receiverId == null) {
            log.warn("帖子作者为空，跳过通知, postId={}", event.getPostId());
            return;
        }
        Long senderId=event.getUserId();
        // 自己给自己点赞不发通知
        if (Objects.equals(receiverId, senderId)) {
            return;
        }

        Long postId=event.getPostId();

        NotificationMessage notification =
                NotificationMessage.builder()
                        .eventId(UUID.randomUUID().toString())
                        .receiverId(receiverId)
                        .senderId(senderId)
                        .type(NotificationType.POST_LIKE.getCode())
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
                                "帖子点赞通知发送成功, postId={}, receiverId={}",
                                postId,
                                receiverId
                        );
                    }

                    @Override
                    public void onException(Throwable e) {

                        log.error(
                                "帖子点赞通知发送失败, postId={}, receiverId={}",
                                postId,
                                receiverId,
                                e
                        );
                    }
                }
        );
    }
    private boolean invalidInteractMessage(PostLikeEvent event){
        return event==null||event.getUserId()==null||
                event.getPostId()==null||event.getLiked()==null;
    }
}
