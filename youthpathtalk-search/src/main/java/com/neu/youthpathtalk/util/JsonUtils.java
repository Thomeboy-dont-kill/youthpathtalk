package com.neu.youthpathtalk.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Julien
 * @time 2026/04/04 11:22
 * @description 非静态Json工具类，复用全局objectMapper
 */
@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper objectMapper;

    //序列化
    @SneakyThrows
    public String toJsonString(Object object){return objectMapper.writeValueAsString(object);}

    //反序列化成指定类类型的对象
    @SneakyThrows//受检异常（非运行时异常）转换成非受检异常（运行时异常），因此不强制开发者处理
    public <T> T parseObject(String jsonStr,Class<T> clazz){
        if (StringUtils.isBlank(jsonStr)){
            return null;
        }
        return objectMapper.readValue(jsonStr,clazz);
    }

    //反序列化Map
    public <K,V> Map<K,V> parseMap(String jsonStr, Class<K> keyClazz, Class<V> valueClazz) throws JsonProcessingException {
        if (StringUtils.isBlank(jsonStr)){
            return null;
        }
        return objectMapper.readValue(jsonStr,objectMapper.getTypeFactory().constructMapType(Map.class,keyClazz,valueClazz));
    }

    //反序列化List集合
    public <T> List<T> parseList(String jsonStr, Class<T> clazz) throws JsonProcessingException{
        if (StringUtils.isBlank(jsonStr)){
            return null;
        }
        return objectMapper.readValue(jsonStr,objectMapper.getTypeFactory().constructCollectionType(List.class,clazz));
    }

    @SuppressWarnings("unchecked")
    public <T> T parseGeneric(String jsonStr, Class<?> rawClass, Class<?>... parameterClasses) throws JsonProcessingException {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(rawClass, parameterClasses);
        return (T) objectMapper.readValue(jsonStr, type);
    }
}
