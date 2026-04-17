package com.neu.youthpathtalk.post.biz.consumer;

import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.mapper.IdempotentMapper;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import com.neu.youthpathtalk.post.biz.message.CommonCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julien
 * @time 2026/03/24 20:05
 * @description 帖子点赞计数消费端
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MQConstants.TOPIC_POST_LIKE_COUNT,
consumerGroup = MQConstants.CONSUMER_GROUP_POST_INTERACTION_COUNT)
public class PostLikeCountConsumer implements RocketMQListener<CommonCountMessage> {
    private final PostMapper postMapper;
    private final IdempotentMapper idempotentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(CommonCountMessage message) {
        if (message==null||message.getId()==null|| message.getTargetId()==null||message.getDelta()==null){
            log.warn("【帖子点赞】无效的聚合计数消息:{}",message);
            return;
        }
        int inserted=idempotentMapper.insertIfNotExist(message.getId());
        if (inserted==0){
            log.debug("【帖子点赞】消息已处理过，跳过:id={}",message);
            return;
        }
        try {
            int rows=postMapper.updateLikeCountById(message.getTargetId(), message.getDelta());
            if (rows==0){
                log.warn("【点赞】帖子不存在或已删除，计数更新失败:postId={},delta:{}",message.getTargetId(),message.getDelta());
            }else {
                log.debug("【点赞】计数更新成功:postId={},delta:{}",message.getTargetId(),message.getDelta());
            }
        } catch (Exception e) {
            log.error("更新帖子点赞计数失败:postId={},delta={}",message.getTargetId(),message.getDelta(),e);
            throw new RuntimeException(e);
        }
    }
}
