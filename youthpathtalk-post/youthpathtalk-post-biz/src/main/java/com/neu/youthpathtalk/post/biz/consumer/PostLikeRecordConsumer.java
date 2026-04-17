package com.neu.youthpathtalk.post.biz.consumer;

import com.google.common.util.concurrent.RateLimiter;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.enums.TargetType;
import com.neu.youthpathtalk.post.biz.event.LikeEvent;
import com.neu.youthpathtalk.post.biz.mapper.LikeRecordMapper;
import lombok.RequiredArgsConstructor;
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
public class PostLikeRecordConsumer implements RocketMQListener<LikeEvent> {
    private final LikeRecordMapper likeRecordMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final RateLimiter rateLimiter;

    public PostLikeRecordConsumer(LikeRecordMapper likeRecordMapper, RocketMQTemplate rocketMQTemplate, @Value("${post.consumer.rate.record.like:5000}") int ratePerSecond) {
        this.likeRecordMapper = likeRecordMapper;
        this.rocketMQTemplate = rocketMQTemplate;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
    }

    @Override
    public void onMessage(LikeEvent likeEvent) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("【帖子点赞记录消费者】达到限流阈值，消息将被重试: {}", likeEvent);
            throw new RuntimeException("Rate limit exceeded, message will be retried");
        }
        if (invalidLikeEvent(likeEvent)){
            log.warn("【帖子】无效的点赞事件:{}",likeEvent);
            return;
        }
        Long userId= likeEvent.getUserId();
        Long postId= likeEvent.getPostId();
        boolean liked=likeEvent.getLiked();
        try {
            if (liked){
                int inserted= likeRecordMapper.insertIgnore(userId, TargetType.POST.getCode(),postId);
                if (inserted>0){
                    log.debug("【帖子】点赞记录入库成功: userId={}, postId={}", userId, postId);
                    Message<LikeEvent> message= MessageBuilder.withPayload(likeEvent).build();
                    String destination=MQConstants.TOPIC_POST_LIKE_COLLECT;
                    rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.debug("==> 【聚合: 帖子点赞】RocketMQ发送成功: userId={}, postId={}, liked={}", userId, postId, liked);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("==> 【聚合: 帖子点赞】RocketMQ发送失败: userId={}, postId={}, liked={}", userId, postId, liked, e);
                        }
                    });
                }else {
                    log.debug("【帖子】重复点赞消息忽略: userId={}, postId={}", userId, postId);
                }
            }else {
                int deleted = likeRecordMapper.deleteByUserIdAndTarget(userId, TargetType.POST.getCode(), postId);
                if (deleted > 0) {
                    log.debug("【帖子】取消点赞记录入库成功: userId={}, postId={}", userId, postId);
                    Message<LikeEvent> message= MessageBuilder.withPayload(likeEvent).build();
                    String destination=MQConstants.TOPIC_POST_LIKE_COLLECT;
                    rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.debug("==> 【聚合: 帖子点赞】RocketMQ发送成功: userId={}, postId={}, liked={}", userId, postId, liked);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("==> 【聚合: 帖子点赞】RocketMQ发送失败: userId={}, postId={}, liked={}", userId, postId, liked, e);
                        }
                    });
                }else {
                    log.debug("【帖子】重复点赞消息忽略: userId={}, postId={}", userId, postId);
                }
            }
        } catch (Exception e) {
            log.error("【帖子】处理点赞消息失败: event={}", likeEvent, e);
            throw new RuntimeException("处理点赞消息失败", e);
        }
    }
    private boolean invalidLikeEvent(LikeEvent event){
        return event==null||event.getUserId()==null||
                event.getPostId()==null||event.getLiked()==null;
    }
}
