package com.neu.youthpathtalk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/05/26 9:52
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum SearchSortType {

    RELEVANCE("综合"),

    CREATE_TIME("最新"),

    VIEW_COUNT("最热");

    private final String label;
}
