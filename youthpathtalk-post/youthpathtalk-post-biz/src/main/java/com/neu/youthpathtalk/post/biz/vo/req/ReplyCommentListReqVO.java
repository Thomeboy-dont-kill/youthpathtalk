package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/05 11:59
 * @description
 */
@Data
@NoArgsConstructor
@Schema(description = "回复评论列表查询请求")
public class ReplyCommentListReqVO {
    @NotNull
    @Schema(
            description = "根评论ID",
            example = "1"
    )
    private Long rootId;

    @Schema(
            description = """
                    游标对象。

                    首页无需传递。

                    下一页请求使用上一页返回的 cursor。
                    """
    )
    private CreateTimeIdCursor cursor;

    @NotNull(message = "分页大小不能为空")
    @Schema(
            description = """
                    每页返回数量。

                    默认10条。
                    """,
            implementation = PageSizeEnum.class,
            example = "SIZE_10"
    )
    private PageSizeEnum pageSize =PageSizeEnum.defaultSize();
}
