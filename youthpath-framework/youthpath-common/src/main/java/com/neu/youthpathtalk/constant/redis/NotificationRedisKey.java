package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

/**
 * @author Julien
 * @time 2026/06/13 10:42
 * @description
 */
public class NotificationRedisKey {
    public static String powerIdempotent(String id){
        return RedisKeyUtil.build(RedisKeyPrefix.NOTIFICATION,"message",id);
    }
    public static String unreadInteraction(String userId){
        return RedisKeyUtil.build(RedisKeyPrefix.NOTIFICATION,RedisKeyPrefix.UNREAD,userId,RedisKeyPrefix.INTERACTION);
    }
    public static String unreadLike(String userId){
        return RedisKeyUtil.build(RedisKeyPrefix.NOTIFICATION,RedisKeyPrefix.UNREAD,userId,RedisKeyPrefix.LIKE);
    }
    public static String unreadFavorite(String userId){
        return RedisKeyUtil.build(RedisKeyPrefix.NOTIFICATION,RedisKeyPrefix.UNREAD,userId,RedisKeyPrefix.FAVORITE);
    }
    public static final long NOTIFICATION_MESSAGE_TTL_SECONDS=5;
}
