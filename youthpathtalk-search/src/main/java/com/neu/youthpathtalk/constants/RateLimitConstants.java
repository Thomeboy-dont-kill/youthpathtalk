package com.neu.youthpathtalk.constants;

/**
 * @author Julien
 * @time 2026/05/12 15:07
 * @description
 */
public final class RateLimitConstants {
    private RateLimitConstants(){}

    public static final int SEARCH_USER_MAX_REQUESTS_PER_MINUTE = 20;

    public static final int SEARCH_IP_MAX_REQUESTS_PER_MINUTE = 10;

    public static final long WINDOW_SIZE_MILLIS = 60_000; // 60秒
    public static final long KEY_EXPIRE_DURATION_SECONDS = 2*60;
}
