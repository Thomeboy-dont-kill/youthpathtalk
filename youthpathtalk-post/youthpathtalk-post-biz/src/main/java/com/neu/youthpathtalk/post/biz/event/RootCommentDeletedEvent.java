package com.neu.youthpathtalk.post.biz.event;

/**
 * @author Julien
 * @time 2026/06/09 13:59
 * @description
 */
public record RootCommentDeletedEvent(Long postId, Long commentId) {
}
