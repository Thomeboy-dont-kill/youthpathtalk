package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/06/12 22:25
 * @description
 */
public final class CommentRedisKey {
    private CommentRedisKey(){}
    public static String like(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.COMMENT,RedisKeyPrefix.LIKE,"bit",id);
    }
    public static String likeCount(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.COMMENT,RedisKeyPrefix.LIKE,"count",id);
    }
    public static String likeLock(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.COMMENT,RedisKeyPrefix.LIKE, RedisKeyPrefix.LOCK,id);
    }
    public static String meta(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.COMMENT,"meta",id);
    }
    public static String plainText(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.COMMENT,"plainText",id);
    }

    public static final long COMMENT_LIKE_BIT_TTL_DAYS=7;
    public static final long COMMENT_LIKE_COUNT_TTL_DAYS=7;
    public static final long COMMENT_META_TTL_HOURS=1;
    public static final long COMMENT_PLAIN_TEXT_TTL_HOURS=1;
}
