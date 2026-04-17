package com.neu.youthpathtalk.user.biz.consumer;

import com.neu.youthpathtalk.user.biz.constants.MQConstants;
import com.neu.youthpathtalk.user.biz.mapper.IdempotentMapper;
import com.neu.youthpathtalk.user.biz.mapper.UserMapper;
import com.neu.youthpathtalk.user.biz.message.CommonCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julien
 * @time 2026/04/01 15:36
 * @description 用户维度点赞计数
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MQConstants.TOPIC_USER_LIKE_COUNT,
consumerGroup = MQConstants.CONSUMER_GROUP_USER_LIKE_COUNT)
public class UserLikeCountConsumer implements RocketMQListener<CommonCountMessage> {
    private final UserMapper userMapper;
    private final IdempotentMapper idempotentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(CommonCountMessage message) {
        if (message==null||message.getId()==null|| message.getTargetId()==null||message.getDelta()==null){
            log.warn("【用户维度点赞】无效的聚合计数消息:{}",message);
            return;
        }
        int inserted=idempotentMapper.insertIfNotExist(message.getId());
        if (inserted==0){
            log.debug("【用户维度点赞】消息已处理过，跳过:id={}",message);
            return;
        }
        try {
            int rows=userMapper.updateTotalLikeCountById(message.getTargetId(), message.getDelta());
            if (rows==0){
                log.warn("【点赞】用户状态异常，计数更新失败:userId={},delta:{}",message.getTargetId(),message.getDelta());
            }else {
                log.debug("【点赞】计数更新成功:userId={},delta:{}",message.getTargetId(),message.getDelta());
            }
        } catch (Exception e) {
            log.error("更新帖子点赞计数失败:userId={},delta={}",message.getTargetId(),message.getDelta(),e);
            throw new RuntimeException(e);
        }
    }
}
