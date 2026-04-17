package com.neu.youthpathtalk.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Julien
 * @time 2026/03/08 14:17
 * @description 自定义手机号格式校验注解
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    String message() default "手机号格式不正确，须为11位数字";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
