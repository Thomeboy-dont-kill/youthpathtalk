package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/04/01 21:33
 * @description 封装执行post_interaction.lua脚本结果的DTO
 */
@Data
@AllArgsConstructor
public class ToggleResultDTO {
    /**
     * 1: 开启
     * -1: 关闭
     * -2: 缓存不存在
     */
    private Long state;//1 互动，-1取消，-2缓存不存在
    private Long count;//最新互动数
}
