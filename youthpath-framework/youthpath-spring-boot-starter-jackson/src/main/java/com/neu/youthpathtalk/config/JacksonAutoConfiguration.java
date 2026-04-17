package com.neu.youthpathtalk.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.neu.youthpathtalk.constant.DateConstants;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.TimeZone;

/**
 * @author Julien
 * @time 2026/03/08 20:40
 * @description 为redis template定制的objectMapper
 */
@AutoConfiguration
public class JacksonAutoConfiguration {
    /**
     * 特点:保留类型信息、允许缓存null、自定义日期格式
     */
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper(){
        ObjectMapper objectMapper=new ObjectMapper();
        //时区设置,防止出现时区偏移(+8小时问题)
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        //日期时间处理
        JavaTimeModule javaTimeModule=new JavaTimeModule();
        //LocalDateTime:yyyy-MM-dd HH:mm:ss
        javaTimeModule.addSerializer(LocalDateTime.class
                ,new LocalDateTimeSerializer(DateConstants.DATE_FORMAT_Y_M_D_H_M_S));
        javaTimeModule.addDeserializer(LocalDateTime.class
                ,new LocalDateTimeDeserializer(DateConstants.DATE_FORMAT_Y_M_D_H_M_S));

        //LocalDate:yyyy-MM-dd
        javaTimeModule.addSerializer(LocalDate.class
                ,new LocalDateSerializer(DateConstants.DATE_FORMAT_Y_M_D));
        javaTimeModule.addDeserializer(LocalDate.class
                ,new LocalDateDeserializer(DateConstants.DATE_FORMAT_Y_M_D));

        //LocalTime:HH:mm:ss
        javaTimeModule.addSerializer(LocalTime.class
                ,new LocalTimeSerializer(DateConstants.DATE_FORMAT_H_M_S));
        javaTimeModule.addDeserializer(LocalTime.class
                ,new LocalTimeDeserializer(DateConstants.DATE_FORMAT_H_M_S));

        // 支持 YearMonth
        javaTimeModule.addSerializer(YearMonth.class
                , new YearMonthSerializer(DateConstants.DATE_FORMAT_Y_M));
        javaTimeModule.addDeserializer(YearMonth.class
                , new YearMonthDeserializer(DateConstants.DATE_FORMAT_Y_M));

        objectMapper.registerModule(javaTimeModule);

        //在JSON中写入类型信息，反序列化时才知道具体类型，防止Redis取出后变成LinkedHashMap,无法强转
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        //忽略未知字段:前端传了新字段，后端没升级不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        //忽略空Bean:防止new Object()这种无属性对象报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        //空值处理:如果设NON_NULL,缓存null会变成{}
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return objectMapper;
    }
}
