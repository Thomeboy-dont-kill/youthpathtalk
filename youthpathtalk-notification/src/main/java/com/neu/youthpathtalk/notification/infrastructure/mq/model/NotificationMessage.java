package com.neu.youthpathtalk.notification.infrastructure.mq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/12 9:58
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    /**
     * 幂等ID
     */
    private String eventId;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 触发者用户ID
     */
    private Long senderId;

    /**
     * 触发者昵称快照
     */
    private String senderName;

    /**
     * 触发者头像快照
     */
    private String senderAvatar;

    /**
     * 通知类型
     *
     * NotificationType
     */
    private Integer type;

    /**
     * 目标类型
     *
     * TargetType
     */
    private Integer targetType;

    /**
     * 目标ID
     *
     * 帖子ID
     */
    private Long postId;

    /**
     * 目标ID
     *
     * 根评论ID
     */
    private Long rootId;

    /**
     * 目标ID
     *
     * 评论ID
     */
    private Long commentId;

    /**
     * 目标标题快照
     *
     * 帖子标题
     */
    private String targetTitle;

    /**
     * 目标内容（纯文本）快照
     *
     * 评论内容
     */
    private String targetContent;

    /**
     * 触发内容（纯文本）快照
     *
     * 评论内容
     * 回复内容
     * @内容
     */
    private String content;

    /**
     * 通知创建时间
     */
    private LocalDateTime createTime;
}
