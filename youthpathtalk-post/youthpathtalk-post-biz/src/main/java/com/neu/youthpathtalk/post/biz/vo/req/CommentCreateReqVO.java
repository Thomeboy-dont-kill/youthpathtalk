package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/02 23:11
 * @description
 */
@Data
@NoArgsConstructor
@Schema(description = "创建评论请求")
public class CommentCreateReqVO {

    @NotNull
    @Schema(
            description = "评论所属帖子ID",
            example = "5"
    )
    private Long postId;

    @NotNull
    @Schema(
            description = """
                    评论富文本内容。

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
                          "type": "mention",
                          "attrs": {
                            "userId": 2001,
                            "username": "Thome"
                          }
                        },
                        {
                          "type": "text",
                          "text": "你现在找到实习没"
                        }
                      ]
                    }
                    """
    )
    private RichTextDoc content;
}
