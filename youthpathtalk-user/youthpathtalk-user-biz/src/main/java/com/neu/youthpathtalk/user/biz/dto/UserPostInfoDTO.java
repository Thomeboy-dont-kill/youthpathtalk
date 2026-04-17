package com.neu.youthpathtalk.user.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/21 13:23
 * @description 用于用户发帖子的用户信息DTO
 */
@Data
@NoArgsConstructor
public class UserPostInfoDTO {
    private String username;
    private String avatar;
    private Long universityId;
}
