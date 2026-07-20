package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/05 11:27
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "回复评论信息")
public class ReplyCommentRespVO {
    @Schema(description = "评论ID", example = "2001")
    private Long id;

    @Schema(description = "评论用户ID", example = "1")
    private Long userId;

    @Schema(description = "评论用户名", example = "Julien")
    private String userName;

    @Schema(description = "用户头像", example = "/default-avatar.png")
    private String userAvatar;

    @Schema(description = "学校ID", example = "1001")
    private Long universityId;

    @Schema(description = "学校名称", example = "东北大学")
    private String universityName;

    @Schema(description = "根评论ID", example = "1")
    private Long rootId;

    @Schema(description = "父评论ID", example = "1")
    private Long parentId;

    @Schema(description = "被回复用户ID", example = "2001")
    private Long replyUserId;

    @Schema(description = "被回复用户名", example = "Thome")
    private String replyUserName;

    @Schema(
            description = """
                    是否显示回复对象。

                    true：显示“回复 @用户”

                    false：直接回复根评论，不显示回复对象。
                    """,
            example = "false"
    )
    private Boolean showReplyUser=Boolean.TRUE;

    @Schema(
            description = """
                    回复内容JSON字符串。

                    需要前端反序列化成 TipTap 富文本 JSON。

                    当 status=0 时，
                    前端需要替换成：

                    [评论已删除]
                    """
    )
    private String content;

    @Schema(description = "点赞数", example = "5")
    private Integer likeCount;

    @Schema(
            description = """
                    评论状态。

                    0：已删除

                    1：正常
                    """,
            example = "1"
    )
    private Integer status;

    @Schema(description = "回复创建时间")
    private LocalDateTime createTime;
}
