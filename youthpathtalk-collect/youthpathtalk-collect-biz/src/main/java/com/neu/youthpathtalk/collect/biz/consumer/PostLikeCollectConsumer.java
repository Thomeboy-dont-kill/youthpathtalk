package com.neu.youthpathtalk.collect.biz.consumer;

import com.neu.youthpathtalk.collect.biz.constants.MQConstants;
import com.neu.youthpathtalk.collect.biz.event.LikeEvent;
import com.neu.youthpathtalk.collect.biz.message.PostLikeCountMessage;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.github.phantomthief.collection.BufferTrigger;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class PostLikeCollectConsumer implements RocketMQListener<LikeEvent> {
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisTemplate<String,String> redisTemplate;
    private final BufferTrigger<LikeEvent> bufferTrigger=BufferTrigger.<LikeEvent>batchBlocking()
            .bufferSize(50000)
            .batchSize(1000)
            .linger(Duration.ofSeconds(1))
            .setConsumerEx(this::collectMessage)
            .build();

    @Override
    public void onMessage(LikeEvent likeEvent) {
        String eventId=likeEvent.getId();
        String key= PostRedisKey.powerIdempotent(eventId);
        try {
            Boolean isFirst=redisTemplate.opsForValue()
                            .setIfAbsent(key,"1",
                                    PostRedisKey.POST_LIKE_MESSAGE_TTL,PostRedisKey.POST_LIKE_MESSAGE_TTL_UNIT);
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
            redisTemplate.delete(key);
            log.error("业务处理失败，已删除幂等标记，消息将重试:{}",eventId,e);
            throw new RuntimeException("业务处理失败",e);
        }
    }

    private void collectMessage(List<LikeEvent> events){
        log.debug("【帖子点赞】聚合消息,size:{}",events.size());
        Map<Long,Long> deltaMap=new HashMap<>();
        for (LikeEvent event:events){
            long delta=event.getLiked()?1L:-1L;
            deltaMap.merge(event.getPostId(),delta,Long::sum);
        }
        for (Map.Entry<Long,Long> entry:deltaMap.entrySet()){
            if (entry.getValue()==0){
                continue;
            }
            String id= UUID.randomUUID().toString();
            PostLikeCountMessage message=new PostLikeCountMessage(id,entry.getKey(),entry.getValue());
            try {
                SendResult sendResult=rocketMQTemplate.syncSend(MQConstants.TOPIC_POST_LIKE_COUNT,message);
                if (sendResult.getSendStatus()!= SendStatus.SEND_OK){
                    log.error("==> 【计数: 帖子点赞】RocketMQ发送失败: postId={}, delta={}, result={}", entry.getKey(), entry.getValue(), sendResult);
                    throw new RuntimeException("【帖子点赞】聚合消息发送失败");
                }
                log.debug("==> 【计数: 帖子点赞】RocketMQ发送成功: postId={}, delta={}", entry.getKey(), entry.getValue());
            } catch (RuntimeException e) {
                log.error("==> 【计数: 帖子点赞】RocketMQ发送异常: postId={}, delta={}", entry.getKey(), entry.getValue());
                throw new RuntimeException(e);
            }
        }
    }
}
