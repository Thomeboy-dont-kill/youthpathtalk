package com.neu.youthpathtalk.vo.resp;

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
public class SearchFacetVO {

    /**
     * facet 类型
     * 板块类型 / 发布时间
     */
    private String type;

    /**
     * 标签列表
     */
    private List<SearchFacetItemVO> items;
}
