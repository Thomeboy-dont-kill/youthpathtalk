package com.neu.youthpathtalk.post.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/24 10:07
 * @description 点赞类型枚举
 */
@Getter
@RequiredArgsConstructor
public enum TargetType {
    POST(0,"帖子"),
    COMMENT(1,"评论"),
    ;
    private final Integer code;
    private final String desc;
}
