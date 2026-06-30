package com.neu.youthpathtalk.compensate.consumer;

import com.neu.youthpathtalk.compensate.cache.RedisService;
import com.neu.youthpathtalk.compensate.constants.MQConstants;
import com.neu.youthpathtalk.compensate.message.InteractCompensateMessage;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/05/28 23:45
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_USER_FAVORITE_HISTORY_COMPENSATE,
        consumerGroup = MQConstants.CONSUMER_GROUP_USER_FAVORITE_HISTORY_COMPENSATE,
        consumeMode = ConsumeMode.ORDERLY
)
public class UserFavoriteHistCompConsumer implements RocketMQListener<InteractCompensateMessage> {
    private final RedisService redisService;

    @Override
    public void onMessage(InteractCompensateMessage message) {
        log.info("收到用户维度收藏记录补偿消息:{}",message);
        String favoriteHistoryKey= UserRedisKey.favoriteHistory(message.getUserId());
        try {
            if (Boolean.TRUE.equals(message.getInteracted())){
                redisService.zAdd(favoriteHistoryKey, message.getTimestamp(), String.valueOf(message.getPostId()));
            }else {
                redisService.zRem(favoriteHistoryKey,String.valueOf(message.getPostId()));
            }
            log.info("补偿消息处理成功: userId={}, postId={}, interacted={}",
                    message.getUserId(),message.getPostId(),message.getInteracted());
        } catch (Exception e) {
            log.error("补偿消息处理失败，将重试",e);
            throw new RuntimeException(e);
        }
    }
}
