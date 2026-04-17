package com.neu.youthpathtalk.exception;

/**
 * @author Julien
 * @time 2026/03/04 16:22
 * @description 基本异常接口
 */
public interface BaseException {
    // 获取异常码
    String getErrorCode();

    // 获取异常信息
    String getErrorMessage();
}
