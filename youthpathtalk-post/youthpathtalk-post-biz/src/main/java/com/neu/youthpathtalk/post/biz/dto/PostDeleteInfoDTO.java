package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/10 13:55
 * @description
 */
@Data
@NoArgsConstructor
public class PostDeleteInfoDTO {
    private Long id;
    private Long userId;
    private Long likeCount;
}