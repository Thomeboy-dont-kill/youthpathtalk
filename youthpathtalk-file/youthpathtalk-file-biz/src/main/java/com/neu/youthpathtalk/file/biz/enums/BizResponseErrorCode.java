package com.neu.youthpathtalk.file.biz.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/22 22:54
 * @description file服务业务异常状态码
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    AUTH_NOT_LOGIN("FILE-20001","用户未登录"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
