package com.neu.youthpathtalk.post.biz.event;

/**
 * @author Julien
 * @time 2026/06/10 15:57
 * @description
 */
public record CommentLikeEvent(
        String eventId,
        Long userId,
        Long commentId,
        Boolean interacted
){}
