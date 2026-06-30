package com.neu.youthpathtalk.notification.common.enums;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Julien
 * @time 2026/03/23 13:00
 * @description 分页大小枚举，限定可选值
 */
@Getter
@RequiredArgsConstructor
public enum PageSizeEnum {
    SIZE_10(10, "10条/页"),
    SIZE_20(20, "20条/页"),
    SIZE_30(30, "30条/页");

    private final int code;
    private final String description;

    /**
     * 获取枚举值对应的代码（用于存储或查询）
     */
    @JsonValue
    public int getCode() {
        return code;
    }

    /**
     * 根据代码反序列化枚举
     */
    @JsonCreator
    public static PageSizeEnum fromCode(int code) {
        Optional<PageSizeEnum> optional = Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst();
        Preconditions.checkArgument(optional.isPresent(),"无效的分页大小: " + code + "，允许的值: 10,20,30,50");
        return optional.get();
    }

    /**
     * 获取默认值
     */
    public static PageSizeEnum defaultSize() {
        return SIZE_10;
    }
}
