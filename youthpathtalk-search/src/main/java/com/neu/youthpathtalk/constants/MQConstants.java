package com.neu.youthpathtalk.constants;

/**
 * @author Julien
 * @time 2026/05/13 12:26
 * @description
 */
public final class MQConstants {
    private MQConstants(){}

    public static final String TOPIC_POST_SEARCH_SYNC = "post_search_sync";
    private static final String CONSUMER_GROUP_PREFIX = "youthpathtalk_group";
    public static final String CONSUMER_GROUP_POST_SEARCH_SYNC =
            CONSUMER_GROUP_PREFIX + "_post_search_sync_consumer";
}
