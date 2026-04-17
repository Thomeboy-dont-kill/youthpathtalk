package com.neu.youthpathtalk.post.biz.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/23 20:48
 * @description 点赞消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent {
    private String id;//幂等ID
    private Long userId;
    private Long postId;
    private Boolean liked;
}
