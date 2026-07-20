package com.neu.youthpathtalk.vo.resp;

import com.neu.youthpathtalk.post.api.vo.PostListVO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/10 17:09
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "帖子搜索响应结果")
public class SearchPostsRespVO {
    @Schema(description = "帖子列表")
    private List<PostListVO> list;

    @ArraySchema(
            schema = @Schema(
                    description = """
                            下一页游标。

                            前端分页时必须原样透传。
                            """
            )
    )
    private List<Object> searchAfter;

    @Schema(
            description = """
                    动态筛选标签。

                    包括：

                    - 板块类型

                    - 发布时间
                    """
    )
    private List<SearchFacetVO> facets;

    @Schema(
            description = """
                    搜索纠错建议。

                    仅当：

                    1. 搜索关键词为纯英文

                    2. 搜索结果为空

                    时才可能返回。

                    前端可提示：

                    您要找的是不是：xxx
                    """,
            example = "redis"
    )
    private String suggestKeyword;
}
