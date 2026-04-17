package com.neu.youthpathtalk.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Julien
 * @time 2026/03/08 14:59
 * @description 自定义用户名格式校验注解
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface Username {
    String message() default "用户名，长度4-20位，支持中文、英文、数字、下划线和短横线";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
