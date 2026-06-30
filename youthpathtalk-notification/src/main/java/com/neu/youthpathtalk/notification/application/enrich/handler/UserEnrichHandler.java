package com.neu.youthpathtalk.notification.application.enrich.handler;

import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import com.neu.youthpathtalk.notification.application.enrich.EnrichHandler;
import com.neu.youthpathtalk.notification.common.enums.EnrichType;
import com.neu.youthpathtalk.notification.common.util.JsonUtils;
import com.neu.youthpathtalk.notification.domain.dto.UserInfoCacheDTO;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mq.model.NotificationMessage;
import com.neu.youthpathtalk.notification.infrastructure.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/06/12 17:54
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEnrichHandler
        implements EnrichHandler {
    private final UserRpcService userRpcService;
    private final RedisService redisService;
    private final JsonUtils jsonUtils;

    @Override
    public EnrichType support() {
        return EnrichType.USER;
    }

    @Override
    public void enrich(NotificationMessage message) {
        Long senderId = message.getSenderId();
        if (senderId == null) {
            return;
        }
        String key = UserRedisKey.info(senderId);
        try {
            String cached = redisService.get(key);
            if (cached != null) {
                if (RedisConstants.NULL_PLACEHOLDER.equals(cached)) {
                    return;
                }
                UserInfoCacheDTO cacheDTO =
                        jsonUtils.parseObject(cached, UserInfoCacheDTO.class);
                if (cacheDTO != null) {
                    message.setSenderName(cacheDTO.getUsername());
                    message.setSenderAvatar(cacheDTO.getUserAvatar());
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Redis读取用户信息失败, senderId={}", senderId, e);
        }
        UserInfoRespVO senderInfo = null;
        try {
            senderInfo = userRpcService.getUserInfo(senderId);
        } catch (Exception e) {
            log.error("RPC获取用户信息失败, senderId={}", senderId, e);
            return;
        }
        if (senderInfo == null) {
            cacheNull(key);
            return;
        }
        message.setSenderName(senderInfo.getUsername());
        message.setSenderAvatar(senderInfo.getUserAvatar());
        try {
            UserInfoCacheDTO cacheDTO = new UserInfoCacheDTO(
                    senderInfo.getUsername(),
                    senderInfo.getUserAvatar()
            );
            redisService.setJson(
                    key,
                    cacheDTO,
                    UserRedisKey.USER_INFO_TTL,
                    UserRedisKey.USER_INFO_TTL_UNIT
            );
        } catch (Exception e) {
            log.error("写入用户信息缓存失败, senderId={}", senderId, e);
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
            log.error("写入用户空值缓存失败, key={}", key, e);
        }
    }
}
