package com.neu.youthpathtalk.notification.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/12 22:40
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum EnrichType {

    /**
     * 补充发送者信息
     */
    USER,

    /**
     * 补充帖子信息
     */
    POST,

    /**
     * 补充评论信息
     */
    COMMENT
}
