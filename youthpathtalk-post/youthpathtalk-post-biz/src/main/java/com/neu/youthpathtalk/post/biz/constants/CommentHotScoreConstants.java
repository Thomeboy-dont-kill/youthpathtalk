package com.neu.youthpathtalk.post.biz.constants;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/04 18:19
 * @description
 */
public final class CommentHotScoreConstants {

    private CommentHotScoreConstants() {}

    /**
     * 回复权重
     */
    public static final int REPLY_WEIGHT = 2;

    /**
     * 时间偏移量
     */
    public static final int TIME_OFFSET_HOURS = 2;

    /**
     * Hacker News 重力因子
     */
    public static final double GRAVITY = 1.5D;

    public static final BigDecimal HOT_COMMENT_MIN_SCORE =
            BigDecimal.valueOf(5);
}
