package com.neu.youthpathtalk.notification.common.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/15 18:01
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    AUTH_NOT_LOGIN("NOTIFICATION-20001","用户未登录"),

    NOTIFICATION_TYPE_UNSUPPORTED("NOTIFICATION-20002","通知类型不支持"),
    NOTIFICATION_CATEGORY_UNSUPPORTED("NOTIFICATION-20003","通知分类不支持"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
