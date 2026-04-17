package com.neu.youthpathtalk.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Julien
 * @time 2026/03/08 15:00
 * @description 密码格式校验器
 */
public class PasswordValidator implements ConstraintValidator<Password,String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$"
    );
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(password)){
            return false;
        }
        int length=password.length();
        if (length<8||length>20){
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
