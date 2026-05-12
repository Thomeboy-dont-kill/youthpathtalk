package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

/**
 * @author Julien
 * @time 2026/05/12 14:24
 * @description
 */
public final class SearchRedisKey {
    private SearchRedisKey(){}

    //如果担心key永不过期可以加String minute做固定时间窗口
    public static String limitUser(Long userId){
        return RedisKeyUtil.build(RedisKeyPrefix.SEARCH,RedisKeyPrefix.LIMIT,"user",userId);
    }
    public static String limitIP(String ip){
        return RedisKeyUtil.build(RedisKeyPrefix.SEARCH,RedisKeyPrefix.LIMIT,"ip",ip);
    }
    public static String slideLimitUser(Long userId){
        return RedisKeyUtil.build(RedisKeyPrefix.SEARCH,RedisKeyPrefix.LIMIT,RedisKeyPrefix.SLIDE,"user",userId);
    }
    public static String slideLimitIP(String ip){
        return RedisKeyUtil.build(RedisKeyPrefix.SEARCH,RedisKeyPrefix.LIMIT,RedisKeyPrefix.SLIDE,"ip",ip);
    }
}
