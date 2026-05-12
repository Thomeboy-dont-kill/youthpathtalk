package com.neu.youthpathtalk.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/22 22:54
 * @description post服务业务异常状态码
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    SEARCH_TOO_FREQUENT("SEARCH-20001", "搜索过于频繁，请稍后再试"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
