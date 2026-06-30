package com.neu.youthpathtalk.notification.application.enrich.handler;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.notification.application.enrich.EnrichHandler;
import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import com.neu.youthpathtalk.notification.infrastructure.rpc.PostRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/06/12 17:54
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostEnrichHandler implements EnrichHandler {
    private final PostRpcService postRpcService;
    private final RedisService redisService;

    @Override
    public EnrichType support() {
        return EnrichType.POST;
    }

    @Override
    public void enrich(NotificationMessage message) {
        Long postId = message.getPostId();
        if (postId == null) {
            return;
        }
        String key= PostRedisKey.title(postId);
        try {
            String cacheResult = redisService.get(key);
            if (StringUtils.isNotBlank(cacheResult)) {
                if (RedisConstants.NULL_PLACEHOLDER.equals(cacheResult)) {
                    return;
                }
                message.setTargetTitle(cacheResult);
                return;
            }
        } catch (Exception e) {
            log.error("Redis读取帖子标题失败, postId={}", postId, e);
        }
        String rpcResult;
        try {
            rpcResult = postRpcService.getPostTitle(postId);
        } catch (Exception e) {
            log.error("RPC获取帖子标题失败, postId={}", postId, e);
            return;
        }
        if (rpcResult==null){
            cacheNull(key);
            return;
        }
        message.setTargetTitle(rpcResult);
        try {
            redisService.set(
                    key,
                    rpcResult,
                    PostRedisKey.POST_TITLE_TTL,
                    PostRedisKey.POST_TITLE_TTL_UNIT
            );
        } catch (Exception e) {
            log.error("写入帖子标题缓存失败, postId={}", postId, e);
        }
    }
    private void cacheNull(String key) {

        try {
            redisService.set(
                    key,
                    RedisConstants.NULL_PLACEHOLDER,
                    RedisConstants.NULL_VALUE_TTL,
                    RedisConstants.NULL_VALUE_TTL_UNIT
            );
        } catch (Exception e) {
            log.error("写入帖子标题空值缓存失败, key={}", key, e);
        }
    }
}
