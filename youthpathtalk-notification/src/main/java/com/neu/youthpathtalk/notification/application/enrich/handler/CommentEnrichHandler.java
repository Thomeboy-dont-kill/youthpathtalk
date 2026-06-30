package com.neu.youthpathtalk.notification.application.enrich.handler;

import com.neu.youthpathtalk.constant.redis.CommentRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.notification.application.enrich.EnrichHandler;
import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.common.util.JsonUtils;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import com.neu.youthpathtalk.notification.infrastructure.rpc.CommentRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/06/12 17:54
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEnrichHandler
        implements EnrichHandler {
    private final CommentRpcService commentRpcService;
    private final RedisService redisService;
    private final JsonUtils jsonUtils;

    @Override
    public EnrichType support() {
        return EnrichType.COMMENT;
    }

    @Override
    public void enrich(NotificationMessage message) {
        Long commentId = message.getCommentId();
        if (commentId == null) {
            return;
        }
        String key = CommentRedisKey.plainText(commentId);
        try {
            String cached = redisService.get(key);
            if (cached != null) {
                if (RedisConstants.NULL_PLACEHOLDER.equals(cached)) {
                    return;
                }
                message.setTargetContent(cached);
                return;
            }
        } catch (Exception e) {
            log.error("Redis读取评论内容（纯文本）失败, commentId={}", commentId, e);
        }
        String plainText = null;
        try {
            plainText = commentRpcService.getCommentContent(commentId);
        } catch (Exception e) {
            log.error("RPC获取评论内容（纯文本）失败, commentId={}", commentId, e);
            return;
        }
        if (plainText == null) {
            cacheNull(key);
            return;
        }
        message.setTargetContent(plainText);
        try {
            redisService.set(
                    key,
                    plainText,
                    CommentRedisKey.COMMENT_PLAIN_TEXT_TTL_HOURS,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.error("写入评论内容（纯文本）缓存失败, commentId={}", commentId, e);
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
            log.error("写入评论内容（纯文本）空值缓存失败, key={}", key, e);
        }
    }
}