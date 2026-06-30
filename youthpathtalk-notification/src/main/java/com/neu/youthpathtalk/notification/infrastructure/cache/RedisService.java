package com.neu.youthpathtalk.notification.infrastructure.cache;

import com.neu.youthpathtalk.notification.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/06/13 10:40
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final JsonUtils jsonUtils;

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("RedisService setIfAbsent 失败, key: {}", key, e);
            throw new RuntimeException("RedisService setIfAbsent error", e);
        }
    }

    public void deleteLenient(String key) {
        try {
            if (key == null || key.isEmpty()) {
                log.warn("RedisService deleteLenient 传入空 key，跳过");
                return;
            }
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                log.debug("RedisService deleteLenient 成功, key: {}", key);
            } else {
                log.warn("RedisService deleteLenient key 不存在: {}", key);
            }
        } catch (Exception e) {
            log.error("RedisService deleteLenient 失败, key: {}", key, e);
        }
    }

    public String get(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("RedisService get 失败, key: {}", key, e);
            throw new RuntimeException("RedisService get error", e);
        }
    }

    public void set(String key,String value,long timeout,TimeUnit unit){
        try {
            redisTemplate.opsForValue().set(key,value,timeout,unit);
        } catch (Exception e) {
            log.error("RedisService set 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService set error", e);
        }
    }

    public void setJson(String key, Object value,long timeout,TimeUnit unit) {
        try {
            String json= jsonUtils.toJsonString(value);

            redisTemplate.opsForValue().set(key, json,timeout,unit);
        } catch (Exception e) {
            log.error("RedisService setJson 序列化失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService setJson error", e);
        }
    }

    public void increment(String key,long delta){
        try {
            redisTemplate.opsForValue().increment(key,delta);
            log.debug("Redis 计数器更新: key={}, delta={}", key, delta);
        } catch (Exception e) {
            log.error("Redis 计数器更新失败: key={}, delta={}", key, delta, e);
        }
    }

    public List<String> mGet(List<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("Redis mGet 失败, keys={}", keys, e);
            throw new RuntimeException("Redis mGet error", e);
        }
    }
}
