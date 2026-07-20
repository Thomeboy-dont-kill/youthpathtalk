package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.validator.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "短信验证码登录请求参数")
public class SmsLoginReqVO {
    @Schema(
            description = "手机号",
            example = "13800138000"
    )
    @PhoneNumber
    private String phone;

    @Schema(
            description = "短信验证码",
            example = "123456"
    )
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
