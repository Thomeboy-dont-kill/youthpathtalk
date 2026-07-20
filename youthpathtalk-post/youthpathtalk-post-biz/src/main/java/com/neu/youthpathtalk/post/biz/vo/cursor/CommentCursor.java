package com.neu.youthpathtalk.post.biz.vo.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/04 22:10
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论游标")
public class CommentCursor {

    @Schema(
            description = "上一页最后一条评论的热度分",
            example = "0.0174"
    )
    private BigDecimal hotScore;

    @Schema(
            description = "上一页最后一条评论ID",
            example = "1"
    )
    private Long id;
}