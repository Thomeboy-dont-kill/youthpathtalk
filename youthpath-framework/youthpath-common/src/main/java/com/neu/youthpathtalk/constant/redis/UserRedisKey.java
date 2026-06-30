package com.neu.youthpathtalk.constant.redis;

import com.neu.youthpathtalk.util.RedisKeyUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
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

    public static String favoriteHistory(Long id){
        return RedisKeyUtil.build(RedisKeyPrefix.USER,RedisKeyPrefix.FAVORITE,"history",id);
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

    public static Date getWeeklyRankExpireTime() {

        LocalDate now = LocalDate.now();

        LocalDate expireDate =
                now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                        .plusWeeks(1);

        LocalDateTime expireTime =
                expireDate.atStartOfDay();

        return Date.from(
                expireTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }
    public static String weeklyRankVO(int limit) {
        return RedisKeyUtil.build(RedisKeyPrefix.USER, "weekly", "rank","vo",limit);
    }
    public static String weeklyRankVOLock(int limit) {
        return RedisKeyUtil.build(RedisKeyPrefix.USER, "weekly", "rank","vo",RedisKeyPrefix.LOCK,limit);
    }
    public static String info(Long id){
        return RedisKeyUtil.build(
                RedisKeyPrefix.USER,
                RedisKeyPrefix.INFO,
                id
        );
    }
    // ========== TTL 常量 ==========
    //验证码过期时间和发送验证码间隔时间(单位:分钟)
    public static final long VERIFY_CODE_TTL=5;
    public static final long VERIFY_CODE_SEND_INTERVAL=1;
    public static final TimeUnit VERIFY_CODE_TTL_UNIT = TimeUnit.MINUTES;
    public static final long USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL=1;
    public static final TimeUnit USER_LIKE_COUNT_DECREMENT_MESSAGE_TTL_UNIT = TimeUnit.HOURS;
    public static final long USER_WEEKLY_RANK_VO_TTL=5;
    public static final TimeUnit USER_WEEKLY_RANK_VO_TTL_UNIT = TimeUnit.MINUTES;
    //时间会不会太短
    public static final long USER_WEEKLY_RANK_VO_LOCK_TTL=1;
    public static final TimeUnit USER_WEEKLY_RANK_VO_LOCK_TTL_UNIT = TimeUnit.SECONDS;
    public static final long USER_INFO_TTL=1;
    public static final TimeUnit USER_INFO_TTL_UNIT = TimeUnit.HOURS;
}
