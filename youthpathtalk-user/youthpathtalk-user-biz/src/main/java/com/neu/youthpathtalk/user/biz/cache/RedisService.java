package com.neu.youthpathtalk.user.biz.cache;

import com.neu.youthpathtalk.constant.redis.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/04/06 12:42
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public Set<ZSetOperations.TypedTuple<String>> getRecentViewHistory(String viewHistoryKey){
        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples=redisTemplate.opsForZSet()
                    .reverseRangeWithScores(viewHistoryKey,0, RedisConstants.MAX_HISTORY_SIZE-1);
            return Objects.isNull(tuples)?Collections.emptySet():tuples;
        } catch (Exception e) {
            log.error("RedisService getRecentViewHistory 失败,viewHistoryKey:{}",viewHistoryKey);
            throw new RuntimeException("RedisService getRecentViewHistory error",e);
        }
    }

    public Set<ZSetOperations.TypedTuple<String>> zRevRangeWithScores(String key,long start,long end){
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
}
