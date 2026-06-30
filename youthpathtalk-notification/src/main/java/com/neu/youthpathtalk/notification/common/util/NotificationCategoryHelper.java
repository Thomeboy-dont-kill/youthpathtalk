package com.neu.youthpathtalk.notification.common.util;

import com.neu.youthpathtalk.constant.redis.NotificationRedisKey;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.notification.common.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.common.enums.NotificationType;

import java.util.Collections;
import java.util.List;

/**
 * @author Julien
 * @time 2026/06/15 16:56
 * @description
 */
public final class NotificationCategoryHelper {

    private NotificationCategoryHelper() {
    }

    public static List<Integer> getTypes(
            NotificationCategory category
    ) {

        if (category == null) {
            return Collections.emptyList();
        }

        return switch (category) {

            case INTERACTION -> List.of(
                    NotificationType.POST_COMMENT.getCode(),
                    NotificationType.COMMENT_REPLY.getCode(),
                    NotificationType.MENTION.getCode()
            );

            case LIKE -> List.of(
                    NotificationType.POST_LIKE.getCode(),
                    NotificationType.COMMENT_LIKE.getCode()
            );

            case FAVORITE -> List.of(
                    NotificationType.POST_FAVORITE.getCode()
            );

            case FOLLOW -> List.of(
                    NotificationType.FOLLOW.getCode()
            );
        };
    }
    public static NotificationCategory getCategory(
            NotificationType type
    ) {

        return switch (type) {

            case
                    POST_COMMENT,
                    COMMENT_REPLY,
                    MENTION
                    -> NotificationCategory.INTERACTION;

            case
                    POST_LIKE,
                    COMMENT_LIKE
                    -> NotificationCategory.LIKE;

            case POST_FAVORITE
                    -> NotificationCategory.FAVORITE;

            case FOLLOW
                    -> NotificationCategory.FOLLOW;
            default -> throw new BizException(
                    BizResponseErrorCode.NOTIFICATION_TYPE_UNSUPPORTED
            );
        };
    }
    public static String getUnreadKey(
            Long userId,
            NotificationCategory category
    ) {

        return switch (category) {

            case INTERACTION ->
                    NotificationRedisKey.unreadInteraction(
                            String.valueOf(userId)
                    );

            case LIKE ->
                    NotificationRedisKey.unreadLike(
                            String.valueOf(userId)
                    );

            case FAVORITE ->
                    NotificationRedisKey.unreadFavorite(
                            String.valueOf(userId)
                    );

            default -> throw new BizException(
                    BizResponseErrorCode.NOTIFICATION_CATEGORY_UNSUPPORTED
            );
        };
    }
}
