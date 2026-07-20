package com.neu.youthpathtalk.notification.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "通知详情")
public class NotificationRespVO {

    /**
     * 通知ID
     */
    @Schema(description = "通知ID", example = "1")
    private Long id;

    /**
     * 通知类型
     */
    @Schema(
            description = """
                通知类型：

                1 = 帖子点赞(POST_LIKE)
                2 = 帖子收藏(POST_FAVORITE)
                3 = 帖子评论(POST_COMMENT)
                4 = 评论回复(COMMENT_REPLY)
                5 = 评论点赞(COMMENT_LIKE)
                6 = @提及(MENTION)
                7 = 用户关注(FOLLOW)
                99 = 系统通知(SYSTEM)
                """,
            example = "6"
    )
    private Integer type;

    /**
     * 目标类型
     */
    @Schema(
            description = """
                    目标类型：
                    1-帖子
                    2-评论
                    """,
            example = "2"
    )
    private Integer targetType;

    /**
     * 帖子ID
     */
    @Schema(description = "关联帖子ID", example = "2001")
    private Long postId;

    /**
     * 根评论ID
     */
    @Schema(description = "关联根评论ID", example = "3001")
    private Long rootId;

    /**
     * 评论ID
     */
    @Schema(description = "关联评论ID", example = "3002")
    private Long commentId;

    /**
     * 发送者ID
     */
    @Schema(description = "通知发送者ID", example = "10086")
    private Long senderId;

    /**
     * 发送者昵称
     */
    @Schema(description = "发送者昵称", example = "Thome")
    private String senderName;

    /**
     * 发送者头像
     */
    @Schema(
            description = "发送者头像URL",
            example = "/default-avatar.png"
    )
    private String senderAvatar;

    /**
     * 帖子标题快照
     */
    @Schema(
            description = "关联帖子标题快照",
            example = "如何准备秋招Java面试"
    )
    private String targetTitle;

    /**
     * 评论内容快照
     */
    @Schema(
            description = "关联评论内容快照",
            example = "@Thome 你现在找到实习没"
    )
    private String targetContent;

    /**
     * 回复内容 / @内容
     */
    @Schema(
            description = """
                    当前通知展示内容。
                    例如：
                    回复内容、@内容等。
                    """,
            example = "@Thome 我已经拿到offer了"
    )
    private String content;

    /**
     * 是否已读
     */
    @Schema(
            description = "是否已读：0-未读，1-已读",
            example = "0"
    )
    private Integer isRead;

    /**
     * 创建时间
     */
    @Schema(
            description = "通知创建时间",
            example = "2026-06-30T10:30:00"
    )
    private LocalDateTime createTime;
}