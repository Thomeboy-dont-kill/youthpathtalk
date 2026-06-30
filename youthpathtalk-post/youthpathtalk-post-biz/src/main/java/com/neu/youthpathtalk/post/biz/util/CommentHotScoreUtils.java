package com.neu.youthpathtalk.post.biz.util;

import com.neu.youthpathtalk.post.biz.constants.CommentHotScoreConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/04 16:46
 * @description
 */
public final class CommentHotScoreUtils {

    private CommentHotScoreUtils() {
    }

    //问题：实时。但是没有触发热度重算的数据行热度不随时间衰减
    public static BigDecimal calculate(
            int likeCount,
            int replyCount,
            LocalDateTime createTime
    ) {

        long hours =
                Duration.between(
                        createTime,
                        LocalDateTime.now()
                ).toHours();

        double numerator =
                likeCount +
                        replyCount * CommentHotScoreConstants.REPLY_WEIGHT;

        double denominator =
                Math.pow(hours + CommentHotScoreConstants.TIME_OFFSET_HOURS, CommentHotScoreConstants.GRAVITY);

        double score =
                numerator / denominator;

        return BigDecimal.valueOf(score)
                .setScale(
                        4,
                        RoundingMode.HALF_UP
                );
    }
}
