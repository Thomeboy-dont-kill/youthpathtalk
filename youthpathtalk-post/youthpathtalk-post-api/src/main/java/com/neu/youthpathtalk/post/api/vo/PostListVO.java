package com.neu.youthpathtalk.post.api.vo;

import com.neu.youthpathtalk.post.api.dto.CommentHotDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/03/21 16:36
 * @description 用于分页展示的PostListVO
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
@Schema(description = "帖子列表项")
public class PostListVO {
    @Schema(description = "帖子ID", example = "5")
    private Long id;

    @Schema(description = "发帖用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "Julien")
    private String username;

    @Schema(description = "用户头像地址")
    private String userAvatar;

    @Schema(description = "所属学校名称", example = "东北大学")
    private String universityName;

    @Schema(
            description = """
                板块类型：
                0-GRAD（考研）
                1-CIVIL（考公）
                2-WORK（工作）
                """,
            example = "2"
    )
    private Integer boardType;

    @Schema(description = """
                板块名称：
                考研
                考公
                工作
                """,
            example = "工作"
    )
    private String boardTypeName;

    @Schema(description = "帖子标题（可能包含高亮HTML标签）")
    private String title;

    @Schema(
            description = """
                内容预览。

                搜索结果中可能包含高亮内容。
                """
    )
    private String preview;           // 内容预览（前50字）

    @Schema(description = "浏览量", example = "123")
    private Integer viewCount;

    @Schema(description = "点赞数", example = "45")
    private Integer likeCount;

    @Schema(description = "评论数", example = "12")
    private Integer commentCount;

    @Schema(description = "收藏数", example = "8")
    private Integer favoriteCount;

    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    @Schema(description = "是否精华：0-否，1-是", example = "1")
    private Integer isEssence;

    @Schema(description = "帖子发布时间")
    private LocalDateTime createTime;

    @Schema(description = "当前用户是否已点赞")
    private Boolean liked;

    @Schema(description = "当前用户是否已收藏")
    private Boolean favorited;

    @Schema(description = "帖子热评")
    private CommentHotDTO hotComment;
}
