package com.neu.youthpathtalk.response;

import com.neu.youthpathtalk.exception.BaseException;
import com.neu.youthpathtalk.exception.BizException;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Julien
 * @time 2026/03/04 17:00
 * @description 统一响应格式
 */
@Data
@Accessors(chain = true)//支持链式调用
public class Response<T> {
    //响应是否成功
    private Boolean isSuccess=true;
    //响应数据
    private T data;
    //响应状态码
    private String errorCode;
    //错误信息
    private String errorMessage;

    private Response(){}
    //响应成功，没有响应数据
    public static <T> Response<T> ok(){return new Response<T>();}
    //响应成功，有响应数据
    public static <T> Response<T> ok(T data){
        return new Response<T>()
                .setData(data);
    }
    //响应失败（业务异常）
    public static <T> Response<T> fail(BizException bizException){
        return new Response<T>()
                .setIsSuccess(false)
                .setErrorCode(bizException.getErrorCode())
                .setErrorMessage(bizException.getErrorMessage());
    }
    //响应失败（其他异常）
    public static <T> Response<T> fail(BaseException baseException){
        return new Response<T>()
                .setIsSuccess(false)
                .setErrorCode(baseException.getErrorCode())
                .setErrorMessage(baseException.getErrorMessage());
    }
    //响应失败（参数校验异常）
    public static <T> Response<T> fail(String errorCode,String errorMessage){
        return new Response<T>()
                .setIsSuccess(false)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }
}
