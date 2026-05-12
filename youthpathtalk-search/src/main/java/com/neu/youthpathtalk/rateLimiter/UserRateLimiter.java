package com.neu.youthpathtalk.rateLimiter;

import com.neu.youthpathtalk.cache.RedisService;
import com.neu.youthpathtalk.constant.redis.SearchRedisKey;
import com.neu.youthpathtalk.constants.RateLimitConstants;
import com.neu.youthpathtalk.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/05/12 14:38
 * @description 先自己写限流后续可用sentinel
 */
@Component
@RequiredArgsConstructor
public class UserRateLimiter {
    private final RedisService redisService;
    public void checkLimit(Long userId,String ip){
        String key;
        int limit;
        if (Objects.nonNull(userId)){
            key= SearchRedisKey.limitUser(userId);
            limit= RateLimitConstants.SEARCH_USER_MAX_REQUESTS_PER_MINUTE;
        }else {
            key=SearchRedisKey.limitIP(ip);
            limit=RateLimitConstants.SEARCH_IP_MAX_REQUESTS_PER_MINUTE;
        }
        Long count=redisService.incrAndExpire(key, 60);
        if (count>limit){
            throw new BizException(BizResponseErrorCode.SEARCH_TOO_FREQUENT);
        }
    }
}
