package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/08 11:56
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteDTO {

    private Long userId;

    /**
     * 根评论为null
     */
    private Long rootId;

    private Long postId;
}