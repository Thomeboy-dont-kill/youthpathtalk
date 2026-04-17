package com.neu.youthpathtalk.post.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/21 22:45
 * @description 避免二值魔法值
 */
@Getter
@RequiredArgsConstructor
public enum BoolEnum {
    FALSE(0, "否"),
    TRUE(1, "是");

    private final Integer code;
    private final String desc;
}
