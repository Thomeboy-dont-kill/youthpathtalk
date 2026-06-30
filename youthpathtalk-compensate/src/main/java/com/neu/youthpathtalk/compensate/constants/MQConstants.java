package com.neu.youthpathtalk.compensate.constants;

/**
 * @author Julien
 * @time 2026/04/07 20:13
 * @description
 */
public final class MQConstants {
    private MQConstants(){}

    public static final String TOPIC_USER_LIKE_HISTORY_COMPENSATE = "user_like_history_compensate";

    public static final String TOPIC_USER_FAVORITE_HISTORY_COMPENSATE = "user_favorite_history_compensate";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";

    public static final String CONSUMER_GROUP_USER_LIKE_HISTORY_COMPENSATE = CONSUMER_GROUP_PREFIX + "_user_like_history_compensate_consumer";
    public static final String CONSUMER_GROUP_USER_FAVORITE_HISTORY_COMPENSATE = CONSUMER_GROUP_PREFIX + "_user_favorite_history_compensate_consumer";
}
