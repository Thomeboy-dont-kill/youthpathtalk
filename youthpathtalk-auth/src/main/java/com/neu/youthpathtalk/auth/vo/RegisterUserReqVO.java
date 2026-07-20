package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.enums.UserType;
import com.neu.youthpathtalk.validator.Password;
import com.neu.youthpathtalk.validator.PhoneNumber;
import com.neu.youthpathtalk.validator.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/08 13:47
 * @description 注册用户请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户注册请求参数")
public class RegisterUserReqVO {
    @Schema(
            description = """
                    用户名。

                    长度：4~20个字符。

                    允许：
                    中文、字母、数字、下划线(_)、中划线(-)
                    """,
            example = "Thome"
    )
    @Username
    private String username;

    @Schema(
            description = """
                    登录密码。

                    长度：8~20位。

                    必须包含：
                    大小写字母、数字和特殊字符(@$!%*?&)
                    """,
            example = "Test123456789!"
    )
    @Password
    private String password;

    @Schema(
            description = "手机号",
            example = "13800138000"
    )
    @PhoneNumber
    private String phone;

    @Schema(
            description = """
                    用户发展方向：

                    GRAD = 考研党

                    CIVIL = 考公党

                    WORK = 工作党
                    
                    OTHERS = 其他
                    """,
            example = "WORK"
    )
    @NotNull(message = "用户类型不能为空")
    private UserType userType;

    @Schema(
            description = "短信验证码",
            example = "123456"
    )
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
