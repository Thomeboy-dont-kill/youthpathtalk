package com.neu.youthpathtalk.util;

import com.neu.youthpathtalk.constant.redis.RedisKeyPrefix;

import java.util.Arrays;

/**
 * @author Julien
 * @time 2026/03/09 9:25
 * @description Redis Key生成工具
 */
public final class RedisKeyUtil {
    private RedisKeyUtil(){}

    /**
     * 构建Key
     */
    public static String build(Object... parts){
        return String.join(RedisKeyPrefix.SEPARATOR,Arrays.stream(parts).map(String::valueOf).toArray(String[]::new));
    }
}
