package com.neu.youthpathtalk.user.api.vo.req;

import com.neu.youthpathtalk.enums.UserType;
import com.neu.youthpathtalk.validator.Password;
import com.neu.youthpathtalk.validator.PhoneNumber;
import com.neu.youthpathtalk.validator.Username;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/08 13:47
 * @description 添加用户请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserReqVO {
    @Username
    private String username;
    @Password
    private String password;
    @PhoneNumber
    private String phone;
    @NotNull(message = "用户类型不能为空")
    private UserType userType;
}
