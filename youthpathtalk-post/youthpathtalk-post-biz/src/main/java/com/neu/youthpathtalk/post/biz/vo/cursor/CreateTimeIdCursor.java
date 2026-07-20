package com.neu.youthpathtalk.post.biz.vo.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/05 11:42
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "回复评论游标")
public class CreateTimeIdCursor {
    @Schema(
            description = "上一页最后一条回复的创建时间"
    )
    private LocalDateTime createTime;

    @Schema(
            description = "上一页最后一条回复ID",
            example = "2001"
    )
    private Long id;
}
