package com.neu.youthpathtalk.notification.domain.vo.resp;

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
@Schema(
        description = """
                通用游标分页返回对象

                首次请求 cursor 传 null。

                如果 hasNext=true，
                下一次请求需要携带当前返回的 cursor。

                如果 hasNext=false，
                表示没有更多数据。
                """
)
public class CursorPageRespVO<T,C> {
    @Schema(
            description = "当前页数据列表"
    )
    private List<T> list;
    @Schema(
            description = "是否还有下一页",
            example = "true"
    )
    private Boolean hasNext;
    @Schema(
            description = """
                    下一页查询游标。

                    当 hasNext=true 时，
                    前端需要在下一次请求时传入该值。

                    首次请求时传 null。
                    """
    )
    private C cursor;
}
