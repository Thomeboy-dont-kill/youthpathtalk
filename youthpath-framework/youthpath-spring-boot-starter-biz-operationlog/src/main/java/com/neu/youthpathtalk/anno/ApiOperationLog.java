package com.neu.youthpathtalk.anno;

import java.lang.annotation.*;

/**
 * @author Julien
 * @time 2026/03/05 8:29
 * @description 自定义API操作日志注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiOperationLog {
    /**
     * API功能描述
     * @return 返回API功能描述字符串
     */
    String description() default "";
}
