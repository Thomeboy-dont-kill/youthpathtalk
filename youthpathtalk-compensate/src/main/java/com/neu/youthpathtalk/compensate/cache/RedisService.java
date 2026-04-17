package com.neu.youthpathtalk.compensate.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/04/07 20:32
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public Boolean zAdd(String key,double score,String value){
        try {
            return redisTemplate.opsForZSet().add(key,value,score);
        } catch (Exception e) {
            log.error("RedisService zAdd 失败,key:{}",key);
            throw new RuntimeException("RedisService zAdd error", e);
        }
    }

    public Long zRem(String key,String... values){
        try {
            return redisTemplate.opsForZSet().remove(key,values);
        } catch (Exception e) {
            log.error("RedisService zRem 失败,key:{},values:{}",key,values);
            throw new RuntimeException("RedisService zRem error", e);
        }
    }
}
