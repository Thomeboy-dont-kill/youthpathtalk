package com.neu.youthpathtalk.user.biz.exception;

import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * @author Julien
 * @time 2026/03/05 16:01
 * @description 全局异常处理器，统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public Response<?> handleBizException(HttpServletRequest request, BizException e){
        log.warn("{} request fail, errorCode: {}, errorMessage: {}",request.getRequestURI(),e.getErrorCode(),e.getErrorMessage());
        return Response.fail(e);
    }

    /**
     * 捕获参数校验异常@Valid @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleMethodArgumentNotValidException(HttpServletRequest request,MethodArgumentNotValidException e){
        String errorCode= CommonResponseErrorCode.PARAM_NOT_VALID.getErrorCode();
        BindingResult bindingResult=e.getBindingResult();
        StringBuilder sb=new StringBuilder(CommonResponseErrorCode.PARAM_NOT_VALID.getErrorMessage()+": ");
        // 获取校验不通过的字段，并组合错误信息，格式为： email 邮箱格式不正确, 当前值: '123124qq.com';
        Optional.of(bindingResult.getFieldErrors()).ifPresent(errors -> errors.forEach(error ->
                sb.append(error.getField())
                        .append(" ")
                        .append(error.getDefaultMessage())
                        .append(", 当前值: '")
                        .append(error.getRejectedValue())
                        .append("'; ")
        ));
        String errorMessage=sb.toString();
        log.warn("{} request error, errorCode: {}, errorMessage: {}", request.getRequestURI(), errorCode, errorMessage);
        return Response.fail(errorCode,errorMessage);
    }

    /**
     * 捕获参数校验异常@Validated
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleConstraintViolationException(HttpServletRequest request,ConstraintViolationException e){
        String errorCode= CommonResponseErrorCode.PARAM_NOT_VALID.getErrorCode();
        StringBuilder sb=new StringBuilder(CommonResponseErrorCode.PARAM_NOT_VALID.getErrorMessage()+": ");
        // 获取校验不通过的字段，并组合错误信息，格式为： createUser.userDTO.phone 手机号格式不正确, 当前值: '12345678901';
        Optional.of(e.getConstraintViolations()).ifPresent(violations -> violations.forEach(violation ->
                sb.append(violation.getPropertyPath().toString())
                        .append(" ")
                        .append(violation.getMessage())
                        .append(", 当前值: '")
                        .append(violation.getInvalidValue())
                        .append("'; ")
        ));
        String errorMessage=sb.toString();
        log.warn("{} request error, errorCode: {}, errorMessage: {}", request.getRequestURI(), errorCode, errorMessage);
        return Response.fail(errorCode,errorMessage);
    }

    /**
     * 捕获guava参数校验异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleIllegalArgumentException(HttpServletRequest request,IllegalArgumentException e){
        String errorCode= CommonResponseErrorCode.PARAM_NOT_VALID.getErrorCode();
        String errorMessage=e.getMessage();
        log.warn("{} request error, errorCode: {}, errorMessage: {}", request.getRequestURI(), errorCode, errorMessage);
        return Response.fail(errorCode,errorMessage);
    }

    /**
     * 捕获其他异常
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleOtherException(HttpServletRequest request,Exception e){
        log.error("{} request error, ", request.getRequestURI(), e);
        return Response.fail(CommonResponseErrorCode.SYSTEM_ERROR);
    }
}
