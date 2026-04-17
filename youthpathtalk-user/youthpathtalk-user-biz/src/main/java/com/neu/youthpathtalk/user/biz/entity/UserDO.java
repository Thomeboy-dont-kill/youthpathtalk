package com.neu.youthpathtalk.user.biz.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description UserDO用于dao层
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
public class UserDO {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private Integer gender;
    private Integer type;
    private String target;
    private String intro;
    private Long universityId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}