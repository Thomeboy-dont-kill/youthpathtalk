package com.neu.youthpathtalk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/05/25 17:57
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum PublishTimeRange {

    ONE_DAY("最近一天", "now-1d/d", "now"),

    ONE_WEEK("最近一周", "now-7d/d", "now"),

    ONE_MONTH("最近一个月", "now-30d/d", "now"),

    OLDER("更早", null, "now-30d/d");

    /**
     * 前端显示名称
     */
    private final String label;

    /**
     * gte
     */
    private final String from;

    /**
     * lte
     */
    private final String to;
}