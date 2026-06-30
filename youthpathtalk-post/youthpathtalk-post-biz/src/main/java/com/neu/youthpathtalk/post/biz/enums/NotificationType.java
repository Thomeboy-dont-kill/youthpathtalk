package com.neu.youthpathtalk.post.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/11 16:00
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum NotificationType {

    POST_LIKE(1),

    POST_FAVORITE(2),

    POST_COMMENT(3),

    COMMENT_REPLY(4),

    COMMENT_LIKE(5),

    MENTION(6),

    FOLLOW(7),

    SYSTEM(99);

    private final Integer code;
}