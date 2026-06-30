package com.neu.youthpathtalk.post.biz.consumer;

import com.github.phantomthief.collection.BufferTrigger;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.event.PostLikeEvent;
import com.neu.youthpathtalk.post.biz.message.CommonCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * @author Julien
 * @time 2026/03/24 17:23
 * @description 聚合帖子点赞消费端
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_POST_LIKE_COLLECT,
        consumerGroup = MQConstants.CONSUMER_GROUP_POST_INTERACTION_COLLECT,
        consumeMode = ConsumeMode.ORDERLY
)
public class PostLikeCollectConsumer implements RocketMQListener<PostLikeEvent> {
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisService redisService;
    private final BufferTrigger<PostLikeEvent> bufferTrigger=BufferTrigger.<PostLikeEvent>batchBlocking()
            .bufferSize(50000)
            .batchSize(1000)
            .linger(Duration.ofSeconds(1))
            .setConsumerEx(this::collectMessage)
            .build();

    @Override
    public void onMessage(PostLikeEvent likeEvent) {
        String eventId=likeEvent.getId();
        String key= PostRedisKey.powerIdempotent(eventId);
        try {
            Boolean isFirst=redisService.setIfAbsent(key,"1",
                                    PostRedisKey.POST_LIKE_MESSAGE_TTL,
                                    PostRedisKey.POST_LIKE_MESSAGE_TTL_UNIT);
            if (Boolean.FALSE.equals(isFirst)){
                log.debug("重复消息，已忽略:messageId={}",eventId);
                return;
            }
        } catch (Exception e) {
            log.error("Redis 幂等检查失败，消息将重试:{}",eventId,e);
            throw new RuntimeException("Redis 幂等检查失败",e);
        }
        try {
            bufferTrigger.enqueue(likeEvent);
            log.info("消息处理成功:{}",eventId);
        } catch (Exception e) {
            redisService.deleteLenient(key);
            log.error("业务处理失败，已删除幂等标记，消息将重试:{}",eventId,e);
            throw new RuntimeException("业务处理失败",e);
        }
    }

    private void collectMessage(List<PostLikeEvent> events) {

        log.debug("【帖子点赞】聚合消息,size:{}", events.size());

        Map<Long, Long> postDeltaMap = new HashMap<>();

        Map<Long, Long> userDeltaMap = new HashMap<>();

        for (PostLikeEvent event : events) {

            long delta = event.getLiked()
                    ? 1L
                    : -1L;

            //视当前情况不做防御性检查
            // 聚合帖子点赞数
            postDeltaMap.merge(
                    event.getPostId(),
                    delta,
                    Long::sum
            );

            // 聚合作者获赞数
            Long authorId = event.getAuthorId();

            //视当前情况做防御性检查
            if (authorId != null) {
                userDeltaMap.merge(
                        authorId,
                        delta,
                        Long::sum
                );
            }else {
                log.warn("出现了authorId为null的PostLikeEvent对象");
            }
        }

        if (!postDeltaMap.isEmpty()) {

            sendBatchMessages(
                    postDeltaMap,
                    MQConstants.TOPIC_POST_LIKE_COUNT
            );
        }

        if (!userDeltaMap.isEmpty()) {

            sendBatchMessages(
                    userDeltaMap,
                    MQConstants.TOPIC_USER_LIKE_COUNT
            );
        }
    }
    private void sendBatchMessages(Map<Long,Long> deltaMap,String topic){
        for(Map.Entry<Long,Long> entry:deltaMap.entrySet()){
            Long delta=entry.getValue();
            if (delta==0){
                continue;
            }
            Long targetId=entry.getKey();

            String id= UUID.randomUUID().toString();
            CommonCountMessage message=new CommonCountMessage(id,targetId,delta);
            try {
                SendResult sendResult=rocketMQTemplate.syncSend(topic,message);
                if (sendResult.getSendStatus()!= SendStatus.SEND_OK){
                    log.error("【聚合: 帖子点赞】RocketMQ发送失败:topic={}, targetId={}, delta={}, result={}", topic, targetId, delta, sendResult);
                    throw new RuntimeException("【帖子点赞】聚合消息发送失败");
                }
                log.debug("【聚合: 帖子点赞】RocketMQ发送成功:topic={}, targetId={}, delta={}", topic, targetId, delta);
            } catch (RuntimeException e) {
                log.error("【聚合: 帖子点赞】RocketMQ发送异常:topic={}, targetId={}, delta={}", topic, targetId, delta);
                throw new RuntimeException(e);
            }
        }
    }
}
