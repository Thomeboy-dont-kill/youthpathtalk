package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/10 15:25
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeTargetDTO {
    private Long postId;

    private Long rootId;
}
