package com.neu.youthpathtalk.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/05/12 12:24
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索自动补全结果")
public class SuggestVO {

    @Schema(
            description = "帖子ID，可用于跳转帖子详情页",
            example = "5"
    )
    private Long id;

    @Schema(
            description = "推荐标题",
            example = "大三怎么找实习"
    )
    private String title;
}