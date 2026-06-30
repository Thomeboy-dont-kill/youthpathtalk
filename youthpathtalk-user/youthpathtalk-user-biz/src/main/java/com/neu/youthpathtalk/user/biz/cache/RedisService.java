package com.neu.youthpathtalk.user.biz.cache;

import com.neu.youthpathtalk.user.biz.dto.ZSetItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/04/06 12:42
 * @description
 */
@Slf4j
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> releaseLockScript;

    public RedisService(StringRedisTemplate redisTemplate,
                        @Qualifier("releaseLockScript")DefaultRedisScript<Long> releaseLockScript) {
        this.redisTemplate = redisTemplate;
        this.releaseLockScript = releaseLockScript;
    }

    public Set<ZSetOperations.TypedTuple<String>> zRevRangeWithScores(String key, long start, long end){
        try {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error("RedisService zRevRangeWithScores 失败,key:{},start:{},end:{}",key,start,end);
            throw new RuntimeException("RedisService zRevRangeWithScores error",e);
        }
    }

    public Long zCard(String key){
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("RedisService zCard 失败,key:{}",key);
            throw new RuntimeException("RedisService zCard error",e);
        }
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("RedisService setIfAbsent 失败, key: {}", key, e);
            throw new RuntimeException("RedisService setIfAbsent error", e);
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

    public void delete(String key){
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("RedisService delete失败,key:{}",key,e);
            throw new RuntimeException("RedisService delete error",e);
        }
    }

    public List<ZSetItemDTO> listZSet(
            String key,
            long start,
            long end
    ) {

        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(
                                key,
                                start,
                                end
                        );

        if (CollectionUtils.isEmpty(tuples)) {
            return Collections.emptyList();
        }

        List<ZSetItemDTO> result = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> tuple : tuples) {

            try {

                result.add(
                        new ZSetItemDTO(
                                Long.parseLong(tuple.getValue()),
                                tuple.getScore()
                        )
                );

            } catch (Exception e) {

                log.warn(
                        "ZSET member 非法, key={}, value={}",
                        key,
                        tuple.getValue()
                );
            }
        }

        return result;
    }


    public String get(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("RedisService get 失败, key: {}", key, e);
            throw new RuntimeException("RedisService get error", e);
        }
    }

    public String tryLock(String lockKey,long timeout,TimeUnit unit){
        try {
            //唯一标识，防止误删
            String value=UUID.randomUUID().toString();
            Boolean success=redisTemplate.opsForValue().setIfAbsent(lockKey,value,timeout,unit);
            if (Boolean.TRUE.equals(success)){
                return value;
            }
            return null;
        } catch (Exception e) {
            log.error("RedisService tryLock 异常, lockKey: {}", lockKey, e);
            throw new RuntimeException("RedisService tryLock error", e);
        }
    }
    public boolean unLock(String lockKey,String value){
        try {
            Long result=redisTemplate.execute(releaseLockScript,Collections.singletonList(lockKey),value);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            log.error("RedisService unLock 异常, lockKey: {}, value: {}", lockKey, value, e);
            return false;
        }
    }

    public Boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("RedisService exists 失败, key: {}", key, e);
            throw new RuntimeException("RedisService exists error", e);
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
}
