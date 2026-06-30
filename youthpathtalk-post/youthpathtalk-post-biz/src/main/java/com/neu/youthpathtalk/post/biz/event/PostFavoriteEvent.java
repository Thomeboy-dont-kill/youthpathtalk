package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/10 17:38
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostFavoriteEvent {
    private String id;//幂等ID
    private Long userId;
    private Long postId;
    private Boolean favorited;
}
