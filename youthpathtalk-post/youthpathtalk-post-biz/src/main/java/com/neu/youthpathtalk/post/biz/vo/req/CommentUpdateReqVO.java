package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/06/06 17:47
 * @description
 */
@Data
public class CommentUpdateReqVO {

    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    @NotNull
    private RichTextDoc content;
}