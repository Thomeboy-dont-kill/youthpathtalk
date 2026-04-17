package com.neu.youthpathtalk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/10 9:03
 * @description 用户类型枚举:考研党,考公党,工作党,其他
 */
@Getter
@RequiredArgsConstructor
public enum UserType {
    GRAD(0,"考研党"),
    CIVIL(1,"考公党"),
    WORK(2,"工作党"),
    OTHERS(3,"其他"),

    ;

    
    private final int type;//对应t_user表字段type
    private final String chinese;

    public static String getUserTypeName(int type) {
        for (UserType userType : UserType.values()) {
            if (userType.getType()==type) {
                return userType.getChinese();
            }
        }
        return null;
    }
}
