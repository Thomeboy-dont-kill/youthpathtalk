package com.neu.youthpathtalk.gateway.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/11 14:38
 * @description 网关处响应异常状态码
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    USER_NOT_LOGIN("GATEWAY-20001","用户未登录"),
    USER_NOT_ALLOWED("GATEWAY-20002","用户未授权"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
