package com.neu.youthpathtalk.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

/**
 * @author Julien
 * @time 2026/05/12 14:43
 * @description
 */

@Slf4j
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> incrementAndExpireScript;
    public RedisService(StringRedisTemplate redisTemplate,
                        @Qualifier("incrementAndExpireScript") DefaultRedisScript<Long> incrementAndExpireScript) {
        this.redisTemplate = redisTemplate;
        this.incrementAndExpireScript = incrementAndExpireScript;
    }

    public Long incrAndExpire(String key, long expireSeconds) {
        try {
            Long result = redisTemplate.execute(incrementAndExpireScript,
                    Collections.singletonList(key),
                    String.valueOf(expireSeconds));
            return result != null ? result : 0L;
        } catch (Exception e) {
            log.error("Redis限流操作失败，降级放行，key={}", key, e);
            return 0L; // 降级：不限流
        }
    }

    public Long cleanAndCountByScore(String key, double minScore, double maxScore) {
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            zset.removeRangeByScore(key, Double.NEGATIVE_INFINITY, minScore);
            Long count = zset.count(key, minScore+ 1, maxScore);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("清理并统计滑动窗口失败，key={}", key, e);
            return 0L;
        }
    }

    public void addRequestToWindow(String key, String value, double score, long expireSeconds) {
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            zset.add(key, value, score);
            redisTemplate.expire(key, Duration.ofSeconds(expireSeconds));
        } catch (Exception e) {
            log.error("滑动窗口记录请求失败，key={}", key, e);
        }
    }
}
