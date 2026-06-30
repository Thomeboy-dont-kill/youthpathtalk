package com.neu.youthpathtalk.notification.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/15 16:07
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum NotificationCategory {

    /**
     * 回复与@
     */
    INTERACTION(1, "回复与@"),

    /**
     * 收到的赞
     */
    LIKE(2, "收到的赞"),

    /**
     * 收藏
     */
    FAVORITE(3, "收藏"),

    /**
     * 新增粉丝（预留）
     */
    FOLLOW(4, "新增粉丝");

    private final Integer code;

    private final String desc;

    public static NotificationCategory of(Integer code) {

        if (code == null) {
            return null;
        }

        for (NotificationCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }

        return null;
    }
}
