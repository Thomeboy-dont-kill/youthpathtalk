package com.neu.youthpathtalk.notification.application.enrich;

import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/06/12 22:14
 * @description
 */
@Component
public class NotificationEnrichManager {
    private final Map<EnrichType, EnrichHandler> handlerMap;

    public NotificationEnrichManager(
            List<EnrichHandler> handlers) {

        this.handlerMap =
                handlers.stream()
                        .collect(Collectors.toMap(
                                EnrichHandler::support,
                                Function.identity()
                        ));
    }

    public void enrich(
            NotificationMessage message,
            List<EnrichType> types) {

        for (EnrichType type : types) {

            EnrichHandler handler =
                    handlerMap.get(type);

            if (handler != null) {
                handler.enrich(message);
            }
        }
    }
}
