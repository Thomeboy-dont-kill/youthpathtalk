package com.neu.youthpathtalk.notification.application.registry;

import com.neu.youthpathtalk.notification.application.handler.NotificationHandler;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/06/12 12:01
 * @description
 */
@Component
public class NotificationHandlerRegistry {

    private final Map<Integer, NotificationHandler> handlerMap;

    public NotificationHandlerRegistry(List<NotificationHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        h -> h.support().getCode(),
                        Function.identity()
                ));
    }

    public NotificationHandler route(NotificationMessage message) {
        return handlerMap.get(message.getType());
    }
}
