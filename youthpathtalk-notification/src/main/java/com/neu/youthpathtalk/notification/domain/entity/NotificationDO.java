package com.neu.youthpathtalk.notification.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/12 17:56
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 接收者用户ID（谁收到通知）
     */
    private Long receiverId;

    /**
     * 触发者用户ID（谁触发行为）
     */
    private Long senderId;

    /**
     * 触发者昵称（冗余快照，用于展示）
     */
    private String senderName;

    /**
     * 触发者头像（冗余快照）
     */
    private String senderAvatar;

    /**
     * 通知类型（对应 NotificationType 枚举的值）
     */
    private Integer type;

    /**
     * 目标类型（TargetType：POST / COMMENT）
     */
    private Integer targetType;

    /**
     * 目标ID（帖子ID）
     */
    private Long postId;

    /**
     * 目标ID（根评论ID）
     */
    private Long rootId;

    /**
     * 目标ID（评论ID）
     */
    private Long commentId;

    /**
     * 目标标题快照（如帖子标题）
     */
    private String targetTitle;

    /**
     * 目标内容（纯文本）快照（如评论内容/帖子摘要）
     */
    private String targetContent;

    /**
     * 触发内容（纯文本）快照（如“评论内容/回复内容/@内容”）
     */
    private String content;

    /**
     * 是否已读：0未读 1已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
