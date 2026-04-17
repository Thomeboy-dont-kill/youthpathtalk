package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Julien
 * @time 2026/03/22 23:10
 * @description 帖子的基础信息包含作者ID和状态
 */
@Data
@NoArgsConstructor
public class PostBasicInfoDTO {
    private Long userId;
    private Integer status;
}
