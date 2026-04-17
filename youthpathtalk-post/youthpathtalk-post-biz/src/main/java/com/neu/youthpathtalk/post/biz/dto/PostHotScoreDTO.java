package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/09 10:40
 * @description 用于计算热度的DTO
 */
@Data
@NoArgsConstructor
public class PostHotScoreDTO {
    private Long id;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer favoriteCount;

    private LocalDateTime createTime;
}
