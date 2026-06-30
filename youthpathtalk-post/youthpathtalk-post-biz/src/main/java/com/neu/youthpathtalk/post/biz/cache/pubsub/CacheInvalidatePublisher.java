package com.neu.youthpathtalk.post.biz.cache.pubsub;

import com.neu.youthpathtalk.constant.redis.RedisChannel;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/04/10 20:55
 * @description 封装发送失效消息的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheInvalidatePublisher {
    private final JsonUtils jsonUtils;
    private final RedisTemplate<String,String> redisTemplate;

    public void publishExistsInvalidate(Collection<Long> postIds){
        if (Objects.isNull(postIds)||postIds.isEmpty()){
            return;
        }
        String json= jsonUtils.toJsonString(postIds);
        try {
            redisTemplate.convertAndSend(RedisChannel.CACHE_INVALIDATE_EXISTS,json);
            log.info("发布缓存失效广播，帖子ID:{}",postIds);
        } catch (Exception e) {
            log.error("发布缓存失效广播失败，帖子ID:{}",postIds,e);
        }
    }
}
