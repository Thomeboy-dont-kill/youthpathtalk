package com.neu.youthpathtalk.user.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/10 22:01
 * @description 用户状态枚举类
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ABNORMAL(0),
    NORMAL(1),
    ;

    private final int status;
}
