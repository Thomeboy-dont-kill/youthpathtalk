package com.neu.youthpathtalk.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author Julien
 * @time 2026/03/08 21:25
 * @description 适配日期序列化格式
 */
public interface DateConstants {
    /**
     * DateTimeFormatter：年-月-日 时：分：秒
     */
    DateTimeFormatter DATE_FORMAT_Y_M_D_H_M_S = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * DateTimeFormatter：年-月-日
     */
    DateTimeFormatter DATE_FORMAT_Y_M_D = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * DateTimeFormatter：时：分：秒
     */
    DateTimeFormatter DATE_FORMAT_H_M_S = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * DateTimeFormatter：年-月
     */
    DateTimeFormatter DATE_FORMAT_Y_M =  java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
}
