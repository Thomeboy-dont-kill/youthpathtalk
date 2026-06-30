package com.neu.youthpathtalk.notification.application.handler;

import com.neu.youthpathtalk.notification.application.enrich.NotificationEnrichManager;
import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.common.enums.NotificationType;
import com.neu.youthpathtalk.notification.common.util.NotificationCategoryHelper;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mapper.NotificationMapper;
import com.neu.youthpathtalk.notification.domain.entity.NotificationDO;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import lombok.RequiredArgsConstructor;

import static io.micrometer.common.util.StringUtils.truncate;

/**
 * @author Julien
 * @time 2026/06/12 17:51
 * @description
 */
@RequiredArgsConstructor
public abstract class AbstractNotificationHandler
        implements NotificationHandler {
    protected final NotificationEnrichManager enrichManager;
    protected final NotificationMapper notificationMapper;
    protected final RedisService redisService;

    @Override
    public final void handle(NotificationMessage message) {

        enrichManager.enrich(
                message,
                enrichTypes()
        );

        beforeSave(message);

        NotificationDO notificationDO =
                buildNotificationDO(message);

        notificationMapper.insert(notificationDO);
        NotificationCategory category =
                NotificationCategoryHelper.getCategory(
                        NotificationType.fromCode(message.getType())
                );

        String key =
                NotificationCategoryHelper.getUnreadKey(
                        message.getReceiverId(),
                        category
                );
        //通知是流水，不做幂等
        redisService.increment(key,1L);
        //最终一致性，这里先不补偿
        afterSave(message);
    }

    protected void beforeSave(NotificationMessage message) {
    }

    protected void afterSave(NotificationMessage message) {
    }

    protected NotificationDO buildNotificationDO(
            NotificationMessage message) {

        return NotificationDO.builder()
                .receiverId(message.getReceiverId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderAvatar(message.getSenderAvatar())
                .type(message.getType())
                .targetType(message.getTargetType())
                .postId(message.getPostId())
                .rootId(message.getRootId())
                .commentId(message.getCommentId())
                .targetTitle(message.getTargetTitle())
                .targetContent(message.getTargetContent())
                .content(truncate(message.getContent(),500))
                .createTime(message.getCreateTime())
                .build();
    }
}
