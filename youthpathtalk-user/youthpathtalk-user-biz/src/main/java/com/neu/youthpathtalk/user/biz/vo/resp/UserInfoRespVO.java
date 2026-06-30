package com.neu.youthpathtalk.user.biz.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/21 12:24
 * @description 用户信息响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRespVO {
    private String username;
    private String userAvatar;
    private Long universityId;
    private String universityName;
}
