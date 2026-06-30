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
public class PostLikeHandler
        extends AbstractNotificationHandler {
    public PostLikeHandler(
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
        return NotificationType.POST_LIKE;
    }

    @Override
    public List<EnrichType> enrichTypes() {

        return List.of(
                EnrichType.USER,
                EnrichType.POST
        );
    }
}