package com.neu.youthpathtalk.notification.application.handler;

import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.common.enums.NotificationType;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/12 17:51
 * @description
 */
public interface NotificationHandler {
    NotificationType support();

    List<EnrichType> enrichTypes();

    void handle(NotificationMessage message);
}
