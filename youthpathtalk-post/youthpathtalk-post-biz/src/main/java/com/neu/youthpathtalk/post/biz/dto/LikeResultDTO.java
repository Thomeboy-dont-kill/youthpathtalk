package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/01 21:33
 * @description 封装执行post_like.lua脚本结果的DTO
 */
@Data
@AllArgsConstructor
public class LikeResultDTO {
    private Long liked;//1 点赞，-1取消，-2缓存不存在
    private Long likeCount;//最新点赞数
}
