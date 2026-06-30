package com.neu.youthpathtalk.cache;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.neu.youthpathtalk.constant.redis.SearchRedisKey;
import com.neu.youthpathtalk.constants.SearchConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

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

    @Async("searchHistoryExecutor")
    public void saveHistory(
            Long userId,
            String keyword
    ) {

        if (Objects.isNull(userId)) {
            return;
        }

        keyword = normalizeKeyword(keyword);

        if (StringUtils.isBlank(keyword)) {
            return;
        }

        // 过滤纯符号
        if (!keyword.matches(".*[a-zA-Z0-9\\u4e00-\\u9fa5].*")) {
            return;
        }

        // 长度限制
        if (keyword.length() >
                SearchConstants.SEARCH_HISTORY_MAX_LENGTH) {
            return;
        }

        String key = SearchRedisKey.history(userId);

        try {

            ZSetOperations<String, String> zset =
                    redisTemplate.opsForZSet();

            // 直接更新 score 即可
            zset.add(
                    key,
                    keyword,
                    System.currentTimeMillis()
            );
            redisTemplate.expire(
                    key,
                    Duration.ofDays(
                            SearchRedisKey.HISTORY_TTL_DAYS
                    )
            );
            // 只保留最近20条
            zset.removeRange(
                    key,
                    0,
                    - SearchConstants.SEARCH_HISTORY_MAX_COUNT - 1
            );

        } catch (Exception e) {

            log.error(
                    "保存搜索历史失败，userId={}, keyword={}",
                    userId,
                    keyword,
                    e
            );
        }
    }

    public List<String> listHistory(String key) {

        if (Objects.isNull(key)) {
            return Collections.emptyList();
        }

        try {

            Set<String> result =
                    redisTemplate.opsForZSet()
                            .reverseRange(
                                    key,
                                    0,
                                    SearchConstants.SEARCH_HISTORY_MAX_COUNT - 1
                            );

            if (CollectionUtils.isEmpty(result)) {
                return Collections.emptyList();
            }

            return new ArrayList<>(result);

        } catch (Exception e) {

            log.error("RedisService listHistory，key={}", key, e);
            throw new RuntimeException("RedisService listHistory error",e);
        }
    }

    public void clearHistory(String key) {
        try {

            redisTemplate.delete(key);

        } catch (Exception e) {

            log.error("RedisService clearHistory 失败，key={}", key, e);

            throw new RuntimeException("RedisService clearHistory error",e);
        }
    }

    // keyword 来自历史列表原值，默认无需 normalize
    public void deleteHistory(String key,String keyword) {
        try {
            redisTemplate.opsForZSet()
                    .remove(key,
                            keyword
                    );
        } catch (Exception e) {

            log.error("RedisService deleteHistory 失败，key={}", key, e);

            throw new RuntimeException("RedisService deleteHistory error",e);
        }
    }

    private String normalizeKeyword(String keyword) {

        if (StringUtils.isBlank(keyword)) {
            return null;
        }

        return keyword
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }
}
