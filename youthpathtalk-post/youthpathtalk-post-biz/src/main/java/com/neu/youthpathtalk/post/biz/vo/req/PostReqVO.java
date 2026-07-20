package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 11:31
 * @description 发布帖子VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "发布帖子请求")
public class PostReqVO {
    /**
     * 板块类型：0-考研，1-考公，2-工作
     */
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

    /**
     * 帖子标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    @Schema(
            description = "帖子标题",
            example = "大三怎么找实习"
    )
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    @Schema(
            description = """
                    帖子富文本内容。

                    使用 TipTap 编辑器生成的 JSON 字符串。

                    后端直接存储该 JSON。

                    支持普通文本、@用户、图片等富文本节点。
                    """,
            example = """
                    {
                      "type":"doc",
                      "content":[
                        {
                          "type":"text",
                          "text":"大三找不到实习怎么办"
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
}
