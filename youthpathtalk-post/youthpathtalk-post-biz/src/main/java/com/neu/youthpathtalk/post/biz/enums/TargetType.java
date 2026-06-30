package com.neu.youthpathtalk.post.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/11 20:50
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum TargetType {
    POST(1),
    COMMENT(2),
    ;
    private final Integer code;
}
