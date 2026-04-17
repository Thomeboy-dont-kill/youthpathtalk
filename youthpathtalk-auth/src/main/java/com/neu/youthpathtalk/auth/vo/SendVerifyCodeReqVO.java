package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.auth.enums.VerifyCodeType;
import com.neu.youthpathtalk.validator.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author Julien
 * @time 2026/03/09 15:57
 * @description 发送验证码请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerifyCodeReqVO {
    @PhoneNumber
    private String phone;
    @NotNull(message = "验证码类型不能为空")
    private VerifyCodeType type;
}
