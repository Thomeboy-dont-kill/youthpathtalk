package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.auth.enums.VerifyCodeType;
import com.neu.youthpathtalk.validator.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "发送短信验证码请求参数")
public class SendVerifyCodeReqVO {
    @Schema(
            description = "接收验证码的手机号",
            example = "13800138000"
    )
    @PhoneNumber
    private String phone;

    @Schema(
            description = """
                    验证码业务类型：

                    LOGIN

                    REGISTER
                    """,
            example = "REGISTER"
    )
    @NotNull(message = "验证码类型不能为空")
    private VerifyCodeType type;
}
