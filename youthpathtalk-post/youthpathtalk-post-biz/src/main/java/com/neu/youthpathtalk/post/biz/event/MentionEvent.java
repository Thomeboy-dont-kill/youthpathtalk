package com.neu.youthpathtalk.post.biz.event;

import com.neu.youthpathtalk.post.biz.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/06/12 15:21
 * @description 要改名？
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentionEvent {

    /**
     * 内容类型
     */
    private TargetType targetType;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 根评论ID
     */
    private Long rootId;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 内容发布者
     */
    private Long senderId;

    /**
     * 发布者昵称
     */
    private String senderName;

    /**
     * 发布者头像
     */
    private String senderAvatar;

    private Set<Long> mentionedUserIds;

    /**
     * 原始内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
