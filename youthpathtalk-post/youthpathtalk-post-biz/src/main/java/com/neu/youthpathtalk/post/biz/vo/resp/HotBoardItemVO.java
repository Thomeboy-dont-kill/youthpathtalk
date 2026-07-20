package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/09 18:11
 * @description
 */
@Data
@NoArgsConstructor
@Schema(description = "帖子热榜条目")
public class HotBoardItemVO {
    @Schema(
            description = "帖子ID",
            example = "5"
    )
    private Long id;
    @Schema(
            description = "帖子标题",
            example = "大三怎么找实习"
    )
    private String title;
}
