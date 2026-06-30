package com.neu.youthpathtalk.notification.application.enrich;

import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;

/**
 * @author Julien
 * @time 2026/06/12 22:42
 * @description
 */
public interface EnrichHandler {
    EnrichType support();

    void enrich(NotificationMessage message);
}
