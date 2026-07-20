package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 17:02
 * @description 滑动分页响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游标分页响应结果")
public class CursorPageRespVO<T,C> {

    @Schema(description = "当前页数据列表")
    private List<T> list;

    @Schema(
            description = """
                    是否存在下一页。

                    true：还有下一页
                    false：已经到底
                    """,
            example = "true"
    )
    private Boolean hasNext;

    @Schema(
            description = "游标信息。"
    )
    private C cursor;
}
