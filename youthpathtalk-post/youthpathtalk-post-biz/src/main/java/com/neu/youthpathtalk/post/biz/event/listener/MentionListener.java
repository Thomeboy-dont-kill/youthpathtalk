package com.neu.youthpathtalk.post.biz.event.listener;

import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.enums.NotificationType;
import com.neu.youthpathtalk.post.biz.event.MentionEvent;
import com.neu.youthpathtalk.post.biz.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author Julien
 * @time 2026/06/12 15:30
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentionListener {
    private final RocketMQTemplate rocketMQTemplate;
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(MentionEvent event) {
        if (CollectionUtils.isEmpty(event.getMentionedUserIds())) {
            return;
        }
        for (Long userId : event.getMentionedUserIds()) {

            if (Objects.equals(
                    userId,
                    event.getSenderId()
            )) {
                continue;
            }

            NotificationMessage notification =
                    NotificationMessage.builder()
                            .eventId(UUID.randomUUID().toString())
                            .receiverId(userId)
                            .senderId(event.getSenderId())
                            .senderName(event.getSenderName())
                            .senderAvatar(event.getSenderAvatar())
                            .type(NotificationType.MENTION.getCode())
                            .targetType(event.getTargetType().getCode())
                            .postId(event.getPostId())
                            .rootId(event.getRootId())
                            .commentId(event.getCommentId())
                            .content(event.getContent())
                            .createTime(event.getCreateTime())
                            .build();
            sendNotification(notification);
        }
    }

    private void sendNotification(NotificationMessage message){
        rocketMQTemplate.asyncSend(
                MQConstants.TOPIC_NOTIFICATION,
                message,
                new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {

                        log.debug(
                                "mention通知发送成功, post={}, rootId={}, commentId={}, receiverId={}",
                                message.getPostId(),
                                message.getRootId(),
                                message.getCommentId(),
                                message.getReceiverId()
                        );
                    }

                    @Override
                    public void onException(Throwable e) {

                        log.error(
                                "mention通知发送失败, post={}, rootId={}, commentId={}, receiverId={}",
                                message.getPostId(),
                                message.getRootId(),
                                message.getCommentId(),
                                message.getReceiverId(),
                                e
                        );
                    }
                }
        );
    }
}
