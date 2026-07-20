package com.neu.youthpathtalk.post.biz.vo.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neu.youthpathtalk.post.biz.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/22 21:36
 * @description 更新帖子请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新帖子请求")
public class PostUpdateReqVO {
    @NotNull(message = "帖子ID不能为空")
    @Schema(
            description = "需要更新的帖子ID",
            example = "8"
    )
    private Long id;

    @NotNull(message = "板块类型不能为空")
    @Schema(
            description = """
                    帖子所属板块：

                    GRAD：考研

                    CIVIL：考公

                    WORK：工作
                    """,
            implementation = BoardType.class,
            example = "WORK"
    )
    private BoardType boardType;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    @Schema(
            description = "帖子标题",
            example = "大三怎么找实习"
    )
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(
            description = """
                    帖子富文本内容。

                    使用 TipTap 编辑器生成的 JSON 字符串。

                    后端直接存储该 JSON。

                    支持普通文本、@用户、图片、视频等富文本节点。
                    """,
            example = """
                    {
                      "type":"doc",
                      "content":[
                        {
                          "type":"text",
                          "text":"找不到实习了，寄"
                        },
                        {
                          "type":"mention",
                          "attrs":{
                            "userId":2001,
                            "username":"Thome"
                          }
                        }
                      ]
                    }
                    """
    )
    private String content;

    @JsonIgnore
    @Schema(hidden = true)
    private String plainText;
}
