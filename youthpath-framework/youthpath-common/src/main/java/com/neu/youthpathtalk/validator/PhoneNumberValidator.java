package com.neu.youthpathtalk.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Julien
 * @time 2026/03/08 14:23
 * @description 手机号校验器
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber,String> {
    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        //校验逻辑:正则表达式判断手机号是否为11位数字
        return phoneNumber!=null&&phoneNumber.matches("\\d{11}");
    }
}
