package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/04 19:16
 * @description
 */
@Data
@NoArgsConstructor
public class CommentHotScoreRecalculateDTO {

    private Long id;

    private Long postId;

    private Integer likeCount;

    private Integer replyCount;

    private LocalDateTime createTime;
}