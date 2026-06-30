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
 * @time 2026/04/07 20:23
 * @description 用户点赞历史补偿消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_USER_LIKE_HISTORY_COMPENSATE,
        consumerGroup = MQConstants.CONSUMER_GROUP_USER_LIKE_HISTORY_COMPENSATE,
        consumeMode = ConsumeMode.ORDERLY
)
public class UserLikeHistCompConsumer implements RocketMQListener<InteractCompensateMessage> {
    private final RedisService redisService;

    @Override
    public void onMessage(InteractCompensateMessage message) {
        log.info("收到用户维度点赞记录补偿消息:{}",message);
        String likeHistoryKey= UserRedisKey.likeHistory(message.getUserId());
        try {
            if (Boolean.TRUE.equals(message.getInteracted())){
                redisService.zAdd(likeHistoryKey, message.getTimestamp(), String.valueOf(message.getPostId()));
            }else {
                redisService.zRem(likeHistoryKey,String.valueOf(message.getPostId()));
            }
            log.info("补偿消息处理成功: userId={}, postId={}, interacted={}",
                    message.getUserId(),message.getPostId(),message.getInteracted());
        } catch (Exception e) {
            log.error("补偿消息处理失败，将重试",e);
            throw new RuntimeException(e);
        }
    }
}
