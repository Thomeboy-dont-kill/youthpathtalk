package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Julien
 * @time 2026/03/21 20:54
 * @description 查看帖子详情响应VO
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
@Schema(description = "帖子详情响应对象")
public class PostDetailRespVO {
    @Schema(description = "帖子ID", example = "5")
    private Long id;

    @Schema(description = "发帖用户ID", example = "1")
    private Long userId;

    @Schema(description = "发帖用户名", example = "Julien")
    private String username;

    @Schema(
            description = "发帖用户头像URL",
            example = "/default-avatar.png"
    )
    private String userAvatar;

    @Schema(
            description = "发帖用户所属大学名称",
            example = "东北大学"
    )
    private String universityName;

    @Schema(
            description = """
                    帖子所属板块：

                    0：考研
                    1：考公
                    2：工作
                    """,
            example = "2"
    )
    private Integer boardType;

    @Schema(
            description = "帖子所属板块名称",
            example = "工作"
    )
    private String boardTypeName;

    @Schema(
            description = "帖子标题",
            example = "大三如何找实习"
    )
    private String title;

    @Schema(
            description = "帖子浏览量",
            example = "25"
    )
    private Integer viewCount;

    @Schema(
            description = "帖子点赞数",
            example = "10"
    )
    private Integer likeCount;

    @Schema(
            description = "帖子评论数",
            example = "7"
    )
    private Integer commentCount;

    @Schema(
            description = "帖子收藏数",
            example = "3"
    )
    private Integer favoriteCount;

    @Schema(
            description = "帖子创建时间",
            example = "2026-04-06T22:56:00"
    )
    private LocalDateTime createTime;

    @Schema(
            description = "帖子最后更新时间",
            example = "2026-04-07T08:30:00"
    )
    private LocalDateTime updateTime;

    @Schema(
            description = """
                    帖子正文内容。

                    该字段为 TipTap 生成的富文本 JSON 字符串，
                    前端需要直接使用 TipTap 的 setContent() 进行渲染。
                    """,
            example = """
                    {
                      "type":"doc",
                      "content":[
                        {
                          "type":"text",
                          "text":"找不到实习了，寄"
                        }
                      ]
                    }
                    """
    )
    private String content;

    @Schema(
            description = """
                    当前登录用户是否已点赞该帖子。

                    未登录时固定返回 false。
                    """,
            example = "false"
    )
    private Boolean liked;

    @Schema(
            description = """
                    当前登录用户是否已收藏该帖子。

                    未登录时固定返回 false。
                    """,
            example = "false"
    )
    private Boolean favorited;
}