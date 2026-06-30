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

    public static String firstCommentPage(Long id,int size){
        return RedisKeyUtil.build(
                RedisKeyPrefix.POST,
                RedisKeyPrefix.COMMENT,
                RedisKeyPrefix.PAGE,
                RedisKeyPrefix.FIRST,
                id,
                size
        );
    }

    /**
     * 缓存帖子分页第一页
     * post:page:first:{size}
     */
    public static String firstPage(int size){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.PAGE,RedisKeyPrefix.FIRST,size);
    }

    public static String like(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"bit",id);
    }
    public static String likeCount(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"count",id);
    }
    public static String favorite(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.FAVORITE,"bit",id);
    }
    public static String favoriteCount(Long id) {
        return RedisKeyUtil.build(RedisKeyPrefix.POST, RedisKeyPrefix.FAVORITE, "count", id);
    }
//    public static String likeCounter(Long id){
//        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"counter",id);
//    }
    public static String allLikeCounter(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"counter","*");
    }
    public static String powerIdempotent(String eventId){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE,"message",eventId);
    }
    public static String likeLock(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.LIKE, RedisKeyPrefix.LOCK,id);
    }
    public static String favoriteLock(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.FAVORITE, RedisKeyPrefix.LOCK,id);
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
    public static String firstPageLock(int size){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.PAGE,RedisKeyPrefix.FIRST,RedisKeyPrefix.LOCK,size);
    }
    public static String commentFirstPageLock(Long id,int size) {
        return RedisKeyUtil.build(RedisKeyPrefix.POST, RedisKeyPrefix.COMMENT, RedisKeyPrefix.PAGE, RedisKeyPrefix.FIRST, RedisKeyPrefix.LOCK, id,size);
    }
    public static String hotBoard(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.HOT,"board","all");
    }
    public static String hotBoardTemp(){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,RedisKeyPrefix.HOT,"board","all",RedisKeyPrefix.TEMP);
    }
    public static String hotCommentRank(Long id){
        return RedisKeyUtil.build(
                RedisKeyPrefix.POST,
                RedisKeyPrefix.COMMENT,
                RedisKeyPrefix.HOT,
                id
        );
    }
    public static String author(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,"author",id);
    }
    public static String title(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.POST,"title",id);
    }

    // ========== TTL 常量 ==========
    //帖子分页第一页过期时间(单位:分钟)
    public static final long FIRST_PAGE_TTL=5;
    public static final TimeUnit FIRST_PAGE_TTL_UNIT = TimeUnit.MINUTES;

    public static final long COMMENT_FIRST_PAGE_TTL=5;
    public static final TimeUnit COMMENT_FIRST_PAGE_TTL_UNIT = TimeUnit.MINUTES;

    //帖子点赞计数器过期时间(单位:分钟)
    public static final long POST_LIKE_COUNTER_TTL=5;
    public static final TimeUnit POST_LIKE_COUNTER_TTL_UNIT = TimeUnit.MINUTES;
    //之前是两小时，现在五秒
    public static final long POST_LIKE_MESSAGE_TTL=5;
    public static final TimeUnit POST_LIKE_MESSAGE_TTL_UNIT = TimeUnit.SECONDS;
    public static final long INIT_CACHE_LOCK_TTL=10;
    public static final TimeUnit INIT_CACHE_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_LIKE_BIT_TTL_DAYS=7;
    public static final long POST_LIKE_COUNT_TTL_DAYS=7;
    public static final long POST_FAVORITE_BIT_TTL_DAYS=7;
    public static final long POST_FAVORITE_COUNT_TTL_DAYS=7;
    public static final long POST_EXISTS_TTL=1;
    public static final TimeUnit POST_EXISTS_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HOURLY_TTL=1;
    public static final TimeUnit POST_VIEW_HOURLY_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HOT_LOCK_TTL=5;
    public static final TimeUnit POST_VIEW_HOT_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_FIRST_PAGE_LOCK_TTL=10;
    public static final TimeUnit POST_FIRST_PAGE_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_COMMENT_FIRST_PAGE_LOCK_TTL=5;
    public static final TimeUnit POST_COMMENT_FIRST_PAGE_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_VIEW_HOT_TTL=3600;
    public static final TimeUnit POST_VIEW_HOT_TTL_UNIT = TimeUnit.SECONDS;
    public static final long POST_AUTHOR_TTL=1;
    public static final TimeUnit POST_AUTHOR_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_TITLE_TTL=1;
    public static final TimeUnit POST_TITLE_TTL_UNIT = TimeUnit.HOURS;
    public static final long POST_VIEW_HISTORY_TTL_SECONDS=30*24*3600;
}
