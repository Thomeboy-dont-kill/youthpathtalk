package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.vo.cursor.CommentCursor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Julien
 * @time 2026/06/04 21:17
 * @description
 */
@Data
@NoArgsConstructor
public class CommentListReqVO {

    @NotNull
    private Long postId;

    private CommentCursor cursor;

    @NotNull(message = "分页大小不能为空")
    private PageSizeEnum pageSize = PageSizeEnum.defaultSize();
}