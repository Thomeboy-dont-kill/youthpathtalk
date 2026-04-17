package com.neu.youthpathtalk.user.biz.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:02
 * @description 密码登录并获取userId请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserIdByPwdLoginReqVO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
