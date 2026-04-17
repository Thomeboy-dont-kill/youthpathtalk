package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/03/09 9:18
 * @description 用户模块Redis Key
 */
public final class UserRedisKey {
    private UserRedisKey() {}

    /**
     * 用户注册验证码
     * user:verify:code:{phone}
     */
    public static String verifyCode(String phone){
        return RedisKeyUtil.build(RedisKeyPrefix.USER,RedisKeyPrefix.VERIFY,"code",phone);
    }

    public static String likeCount(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.USER,RedisKeyPrefix.LIKE,"count",id);
    }

    public static String viewHistory(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.USER,RedisKeyPrefix.VIEW,"history",id);
    }

    public static String likeHistory(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.USER,RedisKeyPrefix.LIKE,"history",id);
    }

    public static String idempotentLikeDecrement(String messageId) {
        return RedisKeyUtil.build(RedisKeyPrefix.USER, RedisKeyPrefix.LIKE,"count", "decrement","message", messageId);
    }

    public static String weeklyRank() {
        LocalDate now=LocalDate.now();
        int week=now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year=now.get(IsoFields.WEEK_BASED_YEAR);
        String weekSuffix=String.format("%dW%02d",year,week);
        return RedisKeyUtil.build(RedisKeyPrefix.USER, "weekly", "rank",weekSuffix);
    }
    // ========== TTL 常量 ==========
    //验证码过期时间和发送验证码间隔时间(单位:分钟)
    public static final long VERIFY_CODE_TTL=5;
    public static final long VERIFY_CODE_SEND_INTERVAL=1;
    public static final TimeUnit VERIFY_CODE_TTL_UNIT = TimeUnit.MINUTES;
    public static final long USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL=1;
    public static final TimeUnit USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL_UNIT = TimeUnit.HOURS;
    public static final long USER_WEEKLY_RANK_TTL=8;
    public static final TimeUnit USER_WEEKLY_RANK_TTL_UNIT = TimeUnit.DAYS;
}
