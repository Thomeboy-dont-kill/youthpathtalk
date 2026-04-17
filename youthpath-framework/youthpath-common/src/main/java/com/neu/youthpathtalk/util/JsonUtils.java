package com.neu.youthpathtalk.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Julien
 * @time 2026/03/05 9:17
 * @description 定制JsonUtils
 */
public final class JsonUtils {
    private JsonUtils() {}

    private static ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    //定制OBJECT_MAPPER
    static {
        //反序列化时遇到未知属性不会失败
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        //序列化空对象不会失败
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        //解决Java8+的LocalDateTime序列化和反序列化问题
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    //开发者可以自己定制OBJECT_MAPPER
    public static void init(ObjectMapper objectMapper){OBJECT_MAPPER=objectMapper;}

    //序列化
    @SneakyThrows
    public static String toJsonString(Object object){return OBJECT_MAPPER.writeValueAsString(object);}

    //反序列化成指定类类型的对象
    @SneakyThrows//受检异常（非运行时异常）转换成非受检异常（运行时异常），因此不强制开发者处理
    public static <T> T parseObject(String jsonStr,Class<T> clazz){
        if (StringUtils.isBlank(jsonStr)){
            return null;
        }
        return OBJECT_MAPPER.readValue(jsonStr,clazz);
    }

    //反序列化Map
    public static <K,V> Map<K,V> parseMap(String jsonStr,Class<K> keyClazz,Class<V> valueClazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonStr,OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class,keyClazz,valueClazz));
    }

    //反序列化List集合
    public static <T> List<T> parseList(String jsonStr,Class<T> clazz) throws JsonProcessingException{
        return OBJECT_MAPPER.readValue(jsonStr,OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class,clazz));
    }
}
