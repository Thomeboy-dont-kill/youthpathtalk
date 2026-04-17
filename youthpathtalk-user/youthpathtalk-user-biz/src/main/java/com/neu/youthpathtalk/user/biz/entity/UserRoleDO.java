package com.neu.youthpathtalk.user.biz.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description UserRoleDO用于dao层
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
public class UserRoleDO {
    private Long id;

    private Long userId;

    private Long roleId;

    private LocalDateTime createTime;
}