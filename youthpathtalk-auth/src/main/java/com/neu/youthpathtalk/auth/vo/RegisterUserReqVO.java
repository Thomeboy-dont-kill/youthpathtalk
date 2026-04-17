package com.neu.youthpathtalk.auth.vo;

import com.neu.youthpathtalk.enums.UserType;
import com.neu.youthpathtalk.validator.Password;
import com.neu.youthpathtalk.validator.PhoneNumber;
import com.neu.youthpathtalk.validator.Username;
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
public class RegisterUserReqVO {
    @Username
    private String username;
    @Password
    private String password;
    @PhoneNumber
    private String phone;
    @NotNull(message = "用户类型不能为空")
    private UserType userType;
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
