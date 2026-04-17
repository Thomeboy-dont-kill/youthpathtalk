package com.neu.youthpathtalk.post.biz.constants;

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

    public static final String TOPIC_USER_LIKE_COUNT = "user_like_count";

    public static final String TOPIC_POST_FAVORITE_COUNT = "post_favorite_count";

    public static final String TOPIC_POST_LIKE_RECORD = "post_like_record";

    public static final String TOPIC_POST_FAVORITE_RECORD = "post_favorite_record";

    public static final String TOPIC_USER_LIKE_HISTORY_COMPENSATE = "user_like_history_compensate";

    public static final String TOPIC_USER_LIKE_COUNT_DECREMENT = "user_like_count_decrement";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";

    public static final String CONSUMER_GROUP_POST_INTERACTION_RECORD = CONSUMER_GROUP_PREFIX + "_post_interaction_record_consumer";

    public static final String CONSUMER_GROUP_POST_INTERACTION_COLLECT = CONSUMER_GROUP_PREFIX + "_post_interaction_collect_consumer";

    public static final String CONSUMER_GROUP_POST_INTERACTION_COUNT = CONSUMER_GROUP_PREFIX + "_post_interaction_count_consumer";
}
