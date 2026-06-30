package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/09 11:19
 * @description
 */
public record CommentHotScoreChangedEvent(Long postId, BigDecimal hotScore, Long commentId) {
}
