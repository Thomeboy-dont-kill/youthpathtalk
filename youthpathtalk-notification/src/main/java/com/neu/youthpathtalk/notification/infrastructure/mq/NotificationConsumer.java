package com.neu.youthpathtalk.notification.infrastructure.mq;

import com.google.common.util.concurrent.RateLimiter;
import com.neu.youthpathtalk.constant.redis.NotificationRedisKey;
import com.neu.youthpathtalk.notification.application.registry.NotificationHandlerRegistry;
import com.neu.youthpathtalk.notification.common.constants.MQConstants;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/06/12 17:50
 * @description
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_NOTIFICATION,
        consumerGroup = MQConstants.CONSUMER_GROUP_NOTIFICATION
)
public class NotificationConsumer implements RocketMQListener<NotificationMessage> {
    private final NotificationHandlerRegistry registry;
    private final RedisService redisService;
    private final RateLimiter rateLimiter;

    public NotificationConsumer(
            NotificationHandlerRegistry registry,
            RedisService redisService,
            @Value("${notification.consumer.rate:5000}") int ratePerSecond
    ) {
        this.registry = registry;
        this.redisService=redisService;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
    }

    @Override
    public void onMessage(NotificationMessage message) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("【通知消费者】达到限流阈值，消息将被重试: {}", message);
            throw new RuntimeException("Rate limit exceeded, message will be retried");
        }
        String eventId=message.getEventId();
        String key= NotificationRedisKey.powerIdempotent(eventId);
        try {
            Boolean isFirst=redisService.setIfAbsent(key,"1",
                    NotificationRedisKey.NOTIFICATION_MESSAGE_TTL_SECONDS,
                    TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(isFirst)){
                log.debug("重复消息，已忽略:messageId={}",eventId);
                return;
            }
        } catch (Exception e) {
            log.error("Redis 幂等检查失败，消息将重试:{}",eventId,e);
            throw new RuntimeException("Redis 幂等检查失败",e);
        }
        try {
            registry.route(message).handle(message);
            log.info("消息处理成功:{}",eventId);
        } catch (Exception e) {
            redisService.deleteLenient(key);
            log.error("业务处理失败，已删除幂等标记，消息将重试:{}",eventId,e);
            throw new RuntimeException("业务处理失败",e);
        }
    }
}
