package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/23 20:48
 * @description 互动事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeEvent {
    private String id;//幂等ID
    private Long userId;
    private Long postId;
    private Long authorId;
    private Boolean liked;
}
