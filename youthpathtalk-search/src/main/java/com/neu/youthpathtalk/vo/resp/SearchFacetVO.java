package com.neu.youthpathtalk.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/25 11:13
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "搜索筛选项")
public class SearchFacetVO {

    /**
     * facet 类型
     * 板块类型 / 发布时间
     */
    @Schema(
            description = """
                    Facet 类型。

                    例如：

                    板块类型

                    发布时间
                    """,
            example = "板块类型"
    )
    private String type;

    /**
     * 标签列表
     */
    @Schema(description = "筛选标签列表")
    private List<SearchFacetItemVO> items;
}
