package com.neu.youthpathtalk.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

/**
 * @author Julien
 * @time 2026/05/11 18:08
 * @description
 */
@Data
@AllArgsConstructor
public class FieldMeta {

    /**
     * ES字段名
     */
    private String esField;

    private String extraEsField;

    /**
     * 值转换器
     */
    private Function<String, Object> converter;
}
