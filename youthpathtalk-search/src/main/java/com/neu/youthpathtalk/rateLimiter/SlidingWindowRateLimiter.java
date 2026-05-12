package com.neu.youthpathtalk.rateLimiter;

import com.neu.youthpathtalk.cache.RedisService;
import com.neu.youthpathtalk.constant.redis.SearchRedisKey;
import com.neu.youthpathtalk.constants.RateLimitConstants;
import com.neu.youthpathtalk.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Julien
 * @time 2026/05/12 15:34
 * @description
 */
@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter {
    private final RedisService redisService;

    public void checkLimit(Long userId,String ip){
        String key= Objects.isNull(userId)
                ?SearchRedisKey.slideLimitIP(ip)
                :SearchRedisKey.slideLimitUser(userId);
        long now=System.currentTimeMillis();
        long windowStart=now- RateLimitConstants.WINDOW_SIZE_MILLIS;

        long count=redisService.cleanAndCountByScore(key,windowStart,now);
        int limit=Objects.isNull(userId)
                ?RateLimitConstants.SEARCH_IP_MAX_REQUESTS_PER_MINUTE
                :RateLimitConstants.SEARCH_USER_MAX_REQUESTS_PER_MINUTE;
        if (count>=limit){
            throw new BizException(BizResponseErrorCode.SEARCH_TOO_FREQUENT);
        }
        redisService.addRequestToWindow(key,String.valueOf(now),now,RateLimitConstants.KEY_EXPIRE_DURATION_SECONDS);
    }
}
