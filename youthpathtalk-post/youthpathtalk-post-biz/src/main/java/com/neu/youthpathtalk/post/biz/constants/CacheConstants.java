package com.neu.youthpathtalk.post.biz.constants;

/**
 * @author Julien
 * @time 2026/04/03 17:38
 * @description redis缓存常量类：热点阈值，构建缓存最大重试次数
 */
public final class CacheConstants {
    private CacheConstants(){}
    /* Thresholds */
    public static final int HOT_THRESHOLD = 100;
    public static final int MAX_RETRIES=5;
}
