package com.neu.youthpathtalk.auth.vo;

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
public class PasswordLoginReqVO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
