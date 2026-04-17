package com.neu.youthpathtalk.aspect;

import com.neu.youthpathtalk.anno.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.neu.youthpathtalk.util.JsonUtils;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/03/05 8:32
 * @description 以@ApiOperationLog为切点AOP
 */
@Slf4j
@Aspect
public class ApiOperationLogAspect {
    //以自定义API操作日志注解为切点，凡是被@ApiOperationLog注释的方法，都会执行环绕中的代码
    @Pointcut("@annotation(com.neu.youthpathtalk.anno.ApiOperationLog)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法所属类的类名
        String clazzName=joinPoint.getTarget().getClass().getSimpleName();
        //获取方法名
        String methodName=joinPoint.getSignature().getName();
        //获取方法入参
        Object[] args=joinPoint.getArgs();
        //入参转Json字符串
        String argsJsonStr= Arrays.stream(args).map(JsonUtils::toJsonString).collect(Collectors.joining(","));
        //功能描述信息
        String description=getApiOperationLogDescription(joinPoint);
        //打印请求相关信息
        log.info("====== 请求开始: [{}], 入参: {}, 请求类: {}, 请求方法: {} =================================== ",
                description,argsJsonStr,clazzName,methodName);
        //记录方法开始时间点的时间戳
        long startTime=System.currentTimeMillis();
        //执行切点方法
        Object result=joinPoint.proceed();
        //执行耗时
        long executionTime=System.currentTimeMillis()-startTime;
        log.info("====== 请求结束: [{}], 耗时: {}ms, 出参: {} =================================== ",
                description, executionTime, JsonUtils.toJsonString(result));
        return result;
    }
    private String getApiOperationLogDescription(ProceedingJoinPoint joinPoint){
        MethodSignature methodSignature=(MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ApiOperationLog apiOperationLog=method.getAnnotation(ApiOperationLog.class);
        return apiOperationLog.description();
    }
}
