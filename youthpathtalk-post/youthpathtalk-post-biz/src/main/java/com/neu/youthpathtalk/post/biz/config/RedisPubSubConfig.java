package com.neu.youthpathtalk.post.biz.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neu.youthpathtalk.constant.redis.RedisChannel;
import com.neu.youthpathtalk.post.biz.cache.LocalCacheManager;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/04/10 21:18
 * @description Redis发布订阅配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {
    private final JsonUtils jsonUtils;

    @Bean
    public MessageListener cacheInvalidateListener(LocalCacheManager localCacheManager){
        return ((message, pattern) -> {
            String body=new String(message.getBody(), StandardCharsets.UTF_8);
            try {
                List<Long> postIds=jsonUtils.parseList(body,Long.class);
                if (Objects.isNull(postIds)||postIds.isEmpty()){
                    return;
                }
                localCacheManager.invalidateExistsBatch(postIds);
                log.info("收到缓存失效广播，失效帖子ID:{}",postIds);
            } catch (Exception e) {
                log.error("处理缓存失效消息失败:{}",body,e);
            }
        });
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory,
                                                        MessageListener cacheInvalidateListener){
        RedisMessageListenerContainer container=new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(cacheInvalidateListener,new ChannelTopic(RedisChannel.CACHE_INVALIDATE_EXISTS));
        return container;
    }
}
