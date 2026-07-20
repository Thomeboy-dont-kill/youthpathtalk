package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "编辑评论请求")
public class CommentUpdateReqVO {

    @NotNull(message = "评论ID不能为空")
    @Schema(
            description = "评论ID",
            example = "2001"
    )
    private Long commentId;

    @NotNull

    @Schema(
            description = """
                    评论富文本内容。

                    使用 TipTap 编辑器生成的 JSON 对象。

                    支持：
                    - text 普通文本节点
                    - mention 用户引用节点
                    - image 图片节点
                    """
    )
    private RichTextDoc content;
}