package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/04 21:19
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论信息")
public class CommentRespVO {

    @Schema(description = "评论ID", example = "1")
    private Long id;

    @Schema(description = "评论用户ID", example = "2001")
    private Long userId;

    @Schema(description = "评论用户名", example = "Thome")
    private String userName;

    @Schema(description = "用户头像", example = "/default-avatar.png")
    private String userAvatar;

    @Schema(description = "学校ID", example = "1001")
    private Long universityId;

    @Schema(description = "学校名称", example = "东北大学")
    private String universityName;

    @Schema(
            description = """
                    评论内容（JSON字符串）。

                    需要前端反序列化得到 TipTap 富文本 JSON。
                    """
    )
    private String content;

    @Schema(description = "点赞数", example = "12")
    private Integer likeCount;

    @Schema(
            description = """
                    回复数量
                    
                    replyCount=1则自动调用接口/comment/reply/list直接显示回复列表不折叠，
                    否则显示”共n条回复“（replyCount=0不显示），点击再调用接口/comment/reply/list查看回复列表。
                    """,
            example = "5"
    )
    private Integer replyCount;

    @Schema(description = "评论发布时间")
    private LocalDateTime createTime;

    @Schema(
            description = "评论热度分",
            example = "0.0174"
    )
    private BigDecimal hotScore;
}
