package com.neu.youthpathtalk.notification.application.handler;

import com.neu.youthpathtalk.notification.application.enrich.NotificationEnrichManager;
import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.common.enums.NotificationType;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mapper.NotificationMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/12 17:52
 * @description
 */
@Component
public class PostCommentHandler
        extends AbstractNotificationHandler {
    public PostCommentHandler(
            NotificationEnrichManager enrichManager,
            NotificationMapper notificationMapper,
            RedisService redisService) {

        super(
                enrichManager,
                notificationMapper,
                redisService
        );
    }

    @Override
    public NotificationType support() {
        return NotificationType.POST_COMMENT;
    }

    @Override
    public List<EnrichType> enrichTypes() {

        return List.of(
                EnrichType.POST
        );
    }
}
