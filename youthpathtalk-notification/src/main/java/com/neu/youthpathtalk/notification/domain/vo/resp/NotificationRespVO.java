package com.neu.youthpathtalk.notification.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/15 17:07
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRespVO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     * 目标类型
     */
    private Integer targetType;

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
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者昵称
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 帖子标题快照
     */
    private String targetTitle;

    /**
     * 评论内容快照
     */
    private String targetContent;

    /**
     * 回复内容 / @内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}