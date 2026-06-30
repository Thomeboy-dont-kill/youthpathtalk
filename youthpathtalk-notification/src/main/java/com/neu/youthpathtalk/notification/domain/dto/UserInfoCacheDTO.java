package com.neu.youthpathtalk.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/13 15:40
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoCacheDTO {

    private String username;

    private String userAvatar;
}
