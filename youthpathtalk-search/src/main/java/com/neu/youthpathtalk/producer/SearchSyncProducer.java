package com.neu.youthpathtalk.producer;

import com.neu.youthpathtalk.constants.MQConstants;
import com.neu.youthpathtalk.message.PostSearchSyncMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/05/13 12:47
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchSyncProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public void send(PostSearchSyncMessage message) {

        try {

            Message<PostSearchSyncMessage> mqMessage =
                    MessageBuilder.withPayload(message).build();

            String hashKey = String.valueOf(message.getPostId());

            rocketMQTemplate.asyncSendOrderly(
                    MQConstants.TOPIC_POST_SEARCH_SYNC,
                    mqMessage,
                    hashKey,
                    new SendCallback() {

                        @Override
                        public void onSuccess(SendResult sendResult) {

                            log.info(
                                    "搜索同步消息发送成功 operation={}, postId={}, msgId={}",
                                    message.getOperation(),
                                    message.getPostId(),
                                    sendResult.getMsgId()
                            );
                        }

                        @Override
                        public void onException(Throwable e) {

                            log.error(
                                    "搜索同步消息发送失败 operation={}, postId={}",
                                    message.getOperation(),
                                    message.getPostId(),
                                    e
                            );
                        }
                    }
            );

        } catch (Exception e) {

            log.error(
                    "搜索同步消息发送异常 operation={}, postId={}",
                    message.getOperation(),
                    message.getPostId(),
                    e
            );
        }
    }
}
