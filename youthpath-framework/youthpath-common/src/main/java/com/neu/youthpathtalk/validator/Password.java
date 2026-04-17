package com.neu.youthpathtalk.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Julien
 * @time 2026/03/08 15:00
 * @description 自定义密码格式校验注解
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface Password {
    String message() default "密码，8-20位，必须包含大小写字母、数字和特殊字符(@$!%*?&)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
