package com.neu.youthpathtalk.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/05/25 11:14
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "筛选标签")
public class SearchFacetItemVO {

    /**
     * 实际值
     */
    @Schema(
            description = "实际值",
            example = "2"
    )
    private String key;

    /**
     * 展示名
     */
    @Schema(
            description = "展示名",
            example = "工作"
    )
    private String label;

    /**
     * 数量
     */
    @Schema(
            description = "数量",
            example = "1"
    )
    private Long count;
}