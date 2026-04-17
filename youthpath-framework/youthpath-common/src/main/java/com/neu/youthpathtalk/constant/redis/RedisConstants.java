package com.neu.youthpathtalk.constant.redis;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/04/06 12:49
 * @description
 */
public final class RedisConstants {
    private RedisConstants(){}
    public static final int MAX_HISTORY_SIZE=50;
    public static final int MAX_RANDOM_OFFSET=601;
    public static final String NULL_PLACEHOLDER = "null";
    public static final long NULL_VALUE_TTL = 5;
    public static final TimeUnit NULL_VALUE_TTL_UNIT = TimeUnit.MINUTES;
}
