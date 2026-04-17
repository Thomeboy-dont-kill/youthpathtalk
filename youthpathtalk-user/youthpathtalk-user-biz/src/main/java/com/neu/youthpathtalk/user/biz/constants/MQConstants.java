package com.neu.youthpathtalk.user.biz.constants;

/**
 * @author Julien
 * @time 2026/04/01 15:57
 * @description MQ常量类
 */
public final class MQConstants {
    private MQConstants(){}

    public static final String TOPIC_USER_LIKE_COUNT = "user_like_count";

    public static final String TOPIC_USER_LIKE_COUNT_DECREMENT = "user_like_count_decrement";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";

    public static final String CONSUMER_GROUP_USER_LIKE_COUNT = CONSUMER_GROUP_PREFIX + "_user_like_count_consumer";

    public static final String CONSUMER_GROUP_USER_LIKE_COUNT_DECREMENT = CONSUMER_GROUP_PREFIX + "_user_like_count_decrement_consumer";
}
