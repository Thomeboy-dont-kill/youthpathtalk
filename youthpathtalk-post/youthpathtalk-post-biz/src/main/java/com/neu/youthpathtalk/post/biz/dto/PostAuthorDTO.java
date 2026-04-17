package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/01 14:51
 * @description
 */
@Data
@NoArgsConstructor
public class PostAuthorDTO {
    private Long postId;
    private Long userId;
}
