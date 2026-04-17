package com.neu.youthpathtalk.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Julien
 * @time 2026/03/05 16:29
 * @description 通用异常状态码枚举类
 */
@Getter
@AllArgsConstructor
public enum CommonResponseErrorCode implements BaseException {
    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
