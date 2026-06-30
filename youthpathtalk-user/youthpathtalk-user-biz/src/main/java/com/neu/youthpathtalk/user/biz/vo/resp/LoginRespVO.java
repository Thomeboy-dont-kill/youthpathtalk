package com.neu.youthpathtalk.user.biz.vo.resp;

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
public class LoginRespVO {
    private Long userId;
    private List<String> paths;
}
