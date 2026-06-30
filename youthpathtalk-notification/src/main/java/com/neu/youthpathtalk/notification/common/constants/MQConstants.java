package com.neu.youthpathtalk.notification.common.constants;

/**
 * @author Julien
 * @time 2026/06/12 21:24
 * @description
 */
public final class MQConstants {
    private MQConstants(){}

    public static final String TOPIC_NOTIFICATION = "notification";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";

    public static final String CONSUMER_GROUP_NOTIFICATION = CONSUMER_GROUP_PREFIX+"_notification_consumer";
}
