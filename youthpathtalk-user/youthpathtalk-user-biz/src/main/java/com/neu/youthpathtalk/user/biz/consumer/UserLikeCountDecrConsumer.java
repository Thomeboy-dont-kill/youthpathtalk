package com.neu.youthpathtalk.user.biz.consumer;

import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.user.biz.cache.RedisService;
import com.neu.youthpathtalk.user.biz.constants.MQConstants;
import com.neu.youthpathtalk.user.biz.mapper.UserMapper;
import com.neu.youthpathtalk.user.biz.message.UserLikeCountDecrMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Julien
 * @time 2026/04/10 16:14
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_USER_LIKE_COUNT_DECREMENT,
        consumerGroup = MQConstants.CONSUMER_GROUP_USER_LIKE_COUNT_DECREMENT
)
public class UserLikeCountDecrConsumer implements RocketMQListener<UserLikeCountDecrMessage> {
    private final UserMapper userMapper;
    private final RedisService redisService;

    @Override
    public void onMessage(UserLikeCountDecrMessage message) {
        String messageId=message.getId();
        String key= UserRedisKey.idempotentLikeDecrement(messageId);
        try {
            Boolean isFirst=redisService.setIfAbsent(key,"1",
                            UserRedisKey.USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL,
                            UserRedisKey.USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL_UNIT);
            if (Boolean.FALSE.equals(isFirst)){
                log.debug("重复消息，已忽略:messageId={}",messageId);
                return;
            }
        } catch (Exception e) {
            log.error("Redis 幂等检查失败，消息将重试:{}",messageId,e);
            throw new RuntimeException("Redis 幂等检查失败",e);
        }
        Map<Long,Long> userDeltas=message.getUserDeltas();
        if (userDeltas==null||userDeltas.isEmpty()){
            return;
        }
        int rows=userMapper.batchDecrTotalLikeCount(userDeltas);
        log.debug("批量更新用户获赞数完成，影响行数:{}", rows);
    }
}
