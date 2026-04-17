package com.neu.youthpathtalk.auth.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 11:05
 * @description 业务异常状态码枚举类
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    VERIFY_CODE_INVALID("AUTH-20001","验证码错误或已过期"),
    VERIFY_CODE_SEND_FREQUENTLY("AUTH-20002","验证码发送频繁"),
    VERIFY_CODE_TYPE_INVALID("AUTH-20003","验证码发送类型不合法"),

    USER_PHONE_REGISTERED("AUTH-20004","手机号已注册"),
    USER_PHONE_NOT_REGISTERED("AUTH-20005","手机号未注册"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
