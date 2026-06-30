package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Julien
 * @time 2026/06/08 20:23
 * @description
 */
public record CommentPageCacheEvictEvent(Long postId) {
}
