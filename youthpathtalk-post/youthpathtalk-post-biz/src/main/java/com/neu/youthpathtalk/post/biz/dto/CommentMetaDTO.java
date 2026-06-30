package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/14 8:58
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentMetaDTO {

    /**
     * 评论作者ID
     */
    private Long userId;

    /**
     * 所属帖子ID
     */
    private Long postId;

    /**
     * 所属帖子ID
     */
    private Long rootId;
}