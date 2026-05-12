package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/03/22 11:20
 * @description post服务的RedisKey
 */
public final class PostRedisKey {
    private PostRedisKey(){}

    /**
     * 缓存帖子分页第一页
     * post:page:first:{size}
     */
    public static String firstPage(int size){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.PAGE,"first",size);
    }

    public static String like(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"bit",id);
    }
    public static String likeCount(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"count",id);
    }
    public static String likeCounter(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"counter",id);
    }
    public static String allLikeCounter(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"counter","*");
    }
    public static String powerIdempotent(String id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"message",id);
    }
    public static String likeLock(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE, RedisKeyPrefix.LOCK,id);
    }
    public static String exists(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,"exists",id);
    }
    public static String viewHourly(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.VIEW,"hourly",id);
    }
    public static String viewHot(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.VIEW,"hot",id);
    }
    public static String viewCount(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.VIEW,"count",id);
    }
    public static String allViewCounter(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.VIEW,"count","*");
    }
    public static String viewHotLock(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.VIEW,"hot", RedisKeyPrefix.LOCK,id);
    }
    public static String hotBoard(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.HOT,"board","all");
    }
    public static String hotBoardTemp(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.HOT,"board","all",RedisKeyPrefix.TEMP);
    }
    public static String author(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,"author",id);
    }

    // ========== TTL 常量 ==========
    //帖子分页第一页过期时间(单位:分钟)
    public static final long FIRST_PAGE_TTL=5;
    public static final TimeUnit FIRST_PAGE_TTL_UNIT = TimeUnit.MINUTES;
    //帖子点赞计数器过期时间(单位:分钟)
    public static final long POST_LIKE_COUNTER_TTL=5;
    public static final TimeUnit POST_LIKE_COUNTER_TTL_UNIT = TimeUnit.MINUTES;
    public static final long POST_LIKE_MESSAGE_TTL=2;
    public static final TimeUnit POST_LIKE_MESSAGE_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_LIKE_LOCK_TTL=10;
    public static final TimeUnit POST_LIKE_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_LIKE_BIT_TTL=7;
    public static final TimeUnit POST_LIKE_BIT_TTL_UNIT = TimeUnit.DAYS;
    public static final long POST_LIKE_COUNT_TTL=7;
    public static final TimeUnit POST_LIKE_COUNT_TTL_UNIT = TimeUnit.DAYS;
    public static final long POST_EXISTS_TTL=1;
    public static final TimeUnit POST_EXISTS_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HOURLY_TTL=1;
    public static final TimeUnit POST_VIEW_HOURLY_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HOT_LOCK_TTL=5;
    public static final TimeUnit POST_VIEW_HOT_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_VIEW_HOT_TTL=3600;
    public static final TimeUnit POST_VIEW_HOT_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_AUTHOR_TTL=1;
    public static final TimeUnit POST_AUTHOR_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HISTORY_TTL_SECONDS=30*24*3600;
}
