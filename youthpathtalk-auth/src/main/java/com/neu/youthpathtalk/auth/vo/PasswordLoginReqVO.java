package com.neu.youthpathtalk.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:02
 * @description 密码登录请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账号密码登录请求参数")
public class PasswordLoginReqVO {
    @Schema(
            description = "用户名",
            example = "Thome"
    )
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(
            description = "登录密码",
            example = "Test123456789!"
    )
    @NotBlank(message = "密码不能为空")
    private String password;
}
