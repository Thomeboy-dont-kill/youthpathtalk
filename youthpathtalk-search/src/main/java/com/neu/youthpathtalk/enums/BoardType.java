package com.neu.youthpathtalk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/21 11:43
 * @description 板块类型枚举 0考研 1考公 2工作
 */
@Getter
@RequiredArgsConstructor
public enum BoardType {
    GRAD(0,"考研"),
    CIVIL(1,"考公"),
    WORK(2,"工作"),

    ;

    private final Integer type;
    private final String chinese;

    public static String getBoardTypeName(Integer type) {
        for (BoardType boardType : BoardType.values()) {
            if (boardType.getType().equals(type)) {
                return boardType.getChinese();
            }
        }
        return null;
    }
}
