package com.neu.youthpathtalk.vo.resp;

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
public class SearchFacetItemVO {

    /**
     * 实际值
     */
    private String key;

    /**
     * 展示名
     */
    private String label;

    /**
     * 数量
     */
    private Long count;
}