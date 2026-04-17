package com.neu.youthpathtalk.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Julien
 * @time 2026/03/08 14:59
 * @description 用户名格式校验器
 */
public class UsernameValidator implements ConstraintValidator<Username,String> {
    // 预编译正则，提升性能
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_\\-\u4e00-\u9fa5]+$");

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(username)){
            return false;
        }
        int length=username.length();
        if (length<4||length>20){
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    @Override
    public void initialize(Username constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
