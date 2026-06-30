package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
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
public class ReplyCommentListReqVO {
    @NotNull
    private Long rootId;

    private CreateTimeIdCursor cursor;

    @NotNull(message = "分页大小不能为空")
    private PageSizeEnum pageSize =PageSizeEnum.defaultSize();
}
