package com.neu.youthpathtalk.post.biz.event;

/**
 * @author Julien
 * @time 2026/06/09 13:57
 * @description
 */
public record RootCommentCreatedEvent(Long postId, Long commentId) {
}
