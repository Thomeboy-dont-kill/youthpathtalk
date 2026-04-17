package com.neu.youthpathtalk.collect.biz.constants;

/**
 * @author Julien
 * @time 2026/03/24 9:07
 * @description MQ常量类
 */
public final class MQConstants {
    private MQConstants(){}
    public static final String TOPIC_POST_LIKE_COLLECT = "post_like_collect";

    public static final String TOPIC_POST_FAVORITE_COLLECT = "post_favorite_collect";

    public static final String TOPIC_POST_LIKE_COUNT = "post_like_count";

    public static final String TOPIC_POST_FAVORITE_COUNT = "post_favorite_count";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";

    public static final String CONSUMER_GROUP_POST_INTERACTION_COLLECT = CONSUMER_GROUP_PREFIX + "_post_interaction_collect_consumer";
}
