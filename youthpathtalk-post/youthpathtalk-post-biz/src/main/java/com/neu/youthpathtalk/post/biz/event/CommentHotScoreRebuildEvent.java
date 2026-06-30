package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Julien
 * @time 2026/06/09 11:35
 * @description
 */
public record CommentHotScoreRebuildEvent(Long rootCommentId, Long postId) {
}
