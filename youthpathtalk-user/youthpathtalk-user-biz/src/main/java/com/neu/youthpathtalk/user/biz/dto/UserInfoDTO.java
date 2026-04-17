package com.neu.youthpathtalk.user.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/10 22:44
 * @description 用户信息DTO:id,status,password
 */
@Data
@NoArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String password;
    private Integer status;
}
