package com.neu.youthpathtalk.user.biz.enums;

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
    USER_USERNAME_REGISTERED("USER-20001","用户名已注册"),
    USER_PHONE_REGISTERED("USER-20002","手机号已注册"),
    USER_STATUS_ABNORMAL("USER-20003","账号异常"),

    USER_ROLE_INIT_ERROR("USER-20004","用户角色重复创建"),

    AUTH_LOGIN_FAILED("USER-20005","用户名或密码错误"),
    AUTH_NOT_LOGIN("USER-20006","用户未登录"),
    AUTH_NOT_PERMISSION("USER-20007","无权限"),

    ;

    private final String errorCode;
    private final String errorMessage;
}
