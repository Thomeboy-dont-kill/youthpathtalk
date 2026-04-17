package com.neu.youthpathtalk.collect.biz.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/24 19:46
 * @description 发送给帖子点赞记录的消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeCountMessage {
    private String id;
    private Long postId;
    private Long delta;
}
