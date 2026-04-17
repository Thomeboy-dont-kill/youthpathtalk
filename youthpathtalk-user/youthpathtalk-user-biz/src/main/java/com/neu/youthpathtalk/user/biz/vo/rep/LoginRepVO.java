package com.neu.youthpathtalk.user.biz.vo.rep;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/11 11:49
 * @description 登录成功响应VO
 */
@Data
@NoArgsConstructor
public class LoginRepVO {
    private Long userId;
    private List<String> paths;
}
