package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/02 23:18
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "回复评论请求")
public class CommentReplyReqVO {
    @NotNull(message = "父评论ID不能为空")
    @Schema(
            description = "被回复评论的ID",
            example = "1"
    )
    @NotNull
    private Long parentId;

    @NotNull
    @Schema(
            description = """
                    回复富文本内容。

                    使用 TipTap 编辑器生成 JSON。

                    支持：
                    - text 普通文本节点
                    - mention @用户节点
                    - image 图片节点
                    """,
            example = """
                    {
                      "content": [
                        {
                          "type": "text",
                          "text": "完了"
                        }
                      ]
                    }
                    """
    )
    private RichTextDoc content;
}