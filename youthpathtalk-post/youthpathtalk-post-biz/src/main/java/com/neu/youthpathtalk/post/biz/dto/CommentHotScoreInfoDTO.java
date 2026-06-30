package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/04 17:19
 * @description
 */
@Data
@NoArgsConstructor
public class CommentHotScoreInfoDTO {
    private Long id;

    private Integer likeCount;

    private Integer replyCount;

    private LocalDateTime createTime;
}
