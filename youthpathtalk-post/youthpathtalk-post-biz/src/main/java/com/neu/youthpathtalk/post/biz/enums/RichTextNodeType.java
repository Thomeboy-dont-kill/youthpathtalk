package com.neu.youthpathtalk.post.biz.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/23 10:53
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum RichTextNodeType {

    TEXT("text"),

    MENTION("mention");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RichTextNodeType fromValue(String value) {

        for (RichTextNodeType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "未知富文本节点类型: " + value
        );
    }
}
