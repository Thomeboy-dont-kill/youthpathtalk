package com.neu.youthpathtalk.exception;
import lombok.Getter;

/**
 * @author Julien
 * @time 2026/03/04 16:23
 * @description 自定义业务异常类
 */
@Getter
public class BizException extends RuntimeException{
    private String errorCode;
    private String errorMessage;

    public BizException(BaseException baseException) {
        this.errorCode = baseException.getErrorCode();
        this.errorMessage = baseException.getErrorMessage();
    }
    public BizException(String errorCode,String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
