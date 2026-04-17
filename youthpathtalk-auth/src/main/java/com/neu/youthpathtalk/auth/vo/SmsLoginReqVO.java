package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.validator.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:22
 * @description 短信登录请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsLoginReqVO {
    @PhoneNumber
    private String phone;
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
