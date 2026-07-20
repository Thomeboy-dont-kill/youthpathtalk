package com.neu.youthpathtalk.user.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/06 15:12
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "最近浏览记录")
public class BrowseHistoryVO {

    @Schema(
            description = "帖子ID",
            example = "5"
    )
    private Long id;

    @Schema(
            description = "帖子作者ID",
            example = "1"
    )
    private Long userId;

    @Schema(
            description = "帖子作者昵称",
            example = "Julien"
    )
    private String username;

    @Schema(
            description = "帖子作者头像地址",
            example = "/default-avatar.png"
    )
    private String userAvatar;

    @Schema(
            description = "作者所属学校名称",
            example = "东北大学"
    )
    private String universityName;

    @Schema(
            description = """
                    帖子所属板块：
                    0-考研
                    1-考公
                    2-工作
                    """,
            example = "2"
    )
    private Integer boardType;

    @Schema(
            description = "板块名称",
            example = "工作"
    )
    private String boardTypeName;

    @Schema(
            description = "帖子标题",
            example = "大三怎么找实习"
    )
    private String title;

    @Schema(
            description = "帖子内容预览（最多50字）",
            example = "找不到实习了，寄"
    )
    private String preview;           // 内容预览（前50字）

    @Schema(
            description = "浏览量",
            example = "5"
    )
    private Integer viewCount;

    @Schema(
            description = "点赞数",
            example = "10"
    )
    private Integer likeCount;

    @Schema(
            description = "评论数",
            example = "3"
    )
    private Integer commentCount;

    @Schema(
            description = "收藏数",
            example = "6"
    )
    private Integer favoriteCount;

    @Schema(
            description = "是否置顶：0-否，1-是",
            example = "0"
    )
    private Integer isTop;

    @Schema(
            description = "是否加精：0-否，1-是",
            example = "0"
    )
    private Integer isEssence;

    @Schema(
            description = "帖子最后更新时间",
            example = "2026-04-06T22:56:00"
    )
    private LocalDateTime updateTime;

    @Schema(
            description = "用户浏览该帖子的时间",
            example = "2026-04-06T16:36:19.691"
    )
    private LocalDateTime browseTime;
}
