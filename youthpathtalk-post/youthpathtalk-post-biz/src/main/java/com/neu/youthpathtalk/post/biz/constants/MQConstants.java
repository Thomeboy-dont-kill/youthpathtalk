package com.neu.youthpathtalk.post.biz.constants;

/**
 * @author Julien
 * @time 2026/03/24 9:07
 * @description MQ常量类
 */
public final class MQConstants {
    private MQConstants(){}
    public static final String TOPIC_POST_LIKE_COLLECT = "post_like_collect";

    public static final String TOPIC_POST_LIKE_COUNT = "post_like_count";

    public static final String TOPIC_USER_LIKE_COUNT = "user_like_count";

    public static final String TOPIC_POST_LIKE_RECORD = "post_like_record";

    public static final String TOPIC_POST_FAVORITE_EVENT = "post_favorite_event";

    public static final String TOPIC_COMMENT_LIKE_EVENT = "comment_like_event";

    public static final String TOPIC_USER_LIKE_HISTORY_COMPENSATE = "user_like_history_compensate";

    public static final String TOPIC_USER_FAVORITE_HISTORY_COMPENSATE = "user_favorite_history_compensate";

    public static final String TOPIC_USER_LIKE_COUNT_DECREMENT = "user_like_count_decrement";

    public static final String TOPIC_NOTIFICATION = "notification";

    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";


    public static final String CONSUMER_GROUP_POST_INTERACTION_RECORD = CONSUMER_GROUP_PREFIX + "_post_interaction_record_consumer";

    public static final String CONSUMER_GROUP_POST_INTERACTION_COLLECT = CONSUMER_GROUP_PREFIX + "_post_interaction_collect_consumer";

    public static final String CONSUMER_GROUP_POST_INTERACTION_COUNT = CONSUMER_GROUP_PREFIX + "_post_interaction_count_consumer";
    public static final String CONSUMER_GROUP_POST_FAVORITE = CONSUMER_GROUP_PREFIX + "_post_favorite_consumer";
    public static final String CONSUMER_GROUP_COMMENT_LIKE = CONSUMER_GROUP_PREFIX + "_comment_like_consumer";
}
