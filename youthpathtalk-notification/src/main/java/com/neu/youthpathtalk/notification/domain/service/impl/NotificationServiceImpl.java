package com.neu.youthpathtalk.notification.domain.service.impl;

import com.neu.youthpathtalk.constant.redis.NotificationRedisKey;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.notification.common.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.common.enums.PageSizeEnum;
import com.neu.youthpathtalk.notification.common.util.NotificationCategoryHelper;
import com.neu.youthpathtalk.notification.domain.service.NotificationService;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationListReqVO;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationReadReqVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.CursorPageRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationUnreadCountRespVO;
import com.neu.youthpathtalk.notification.infrastructure.cache.RedisService;
import com.neu.youthpathtalk.notification.infrastructure.mapper.NotificationMapper;
import com.neu.youthpathtalk.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Julien
 * @time 2026/06/15 16:10
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final RedisService redisService;

    @Override
    public Response<CursorPageRespVO<NotificationRespVO, Long>> list(
            NotificationListReqVO req
    ) {
        Long receiverId = LoginUserContextHolder.getUserId();
        if (Objects.isNull(receiverId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        List<Integer> types =
                NotificationCategoryHelper.getTypes(req.getCategory());
        Long cursor = req.getCursor();
        int pageSize =
                Optional.ofNullable(req.getLimit())
                        .orElse(PageSizeEnum.defaultSize())
                        .getCode();
        List<NotificationRespVO> list =
                notificationMapper.selectByReceiverIdAndTypes(
                        receiverId,
                        types,
                        cursor,
                        pageSize+1
                );
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list = list.subList(0,pageSize);
        }
        Long nextCursor = null;
        if (!list.isEmpty()) {
            nextCursor = list
                    .get(list.size() - 1)
                    .getId();
        }
        CursorPageRespVO<NotificationRespVO, Long> resp =
                new CursorPageRespVO<>(
                        list,
                        hasNext,
                        nextCursor
                );
        return Response.ok(resp);
    }

    @Override
    public Response<Void> read(NotificationReadReqVO req) {
        NotificationCategory category=req.getCategory();
        Long receiverId=LoginUserContextHolder.getUserId();

        if (receiverId == null) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        List<Integer> types = NotificationCategoryHelper.getTypes(category);

        try {
            notificationMapper.updateReadByReceiverIdAndTypes(
                    receiverId,
                    types
            );
        } catch (Exception e) {
            log.error("更新通知已读失败, receiverId={}, category={}", receiverId, category, e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }

        try {
            String redisKey = NotificationCategoryHelper.getUnreadKey(
                    receiverId,
                    category
            );

            if (redisKey != null) {
                redisService.deleteLenient(redisKey);
            }

        } catch (Exception e) {
            log.error("清理未读Redis失败, receiverId={}, category={}", receiverId, category, e);
        }
        return Response.ok();
    }

    @Override
    public Response<Boolean> hasUnread() {
        Long userId = LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)) {
            log.debug("用户未登录，降级处理");
            return Response.ok(Boolean.FALSE);
        }
        List<String> keys = List.of(
                NotificationRedisKey.unreadInteraction(String.valueOf(userId)),
                NotificationRedisKey.unreadLike(String.valueOf(userId)),
                NotificationRedisKey.unreadFavorite(String.valueOf(userId))
        );

        List<String> values = null;
        try {
            values = redisService.mGet(keys);
        } catch (Exception e) {
            log.error("Redis异常，降级处理", e);
            return Response.ok(Boolean.FALSE);
        }

        if (values == null || values.isEmpty()) {
            return Response.ok(Boolean.FALSE);
        }

        for (String v : values) {
            if (v != null) {
                return Response.ok(Boolean.TRUE);
            }
        }

        return Response.ok(Boolean.FALSE);
    }

    @Override
    public Response<NotificationUnreadCountRespVO> getUnreadCount() {
        Long userId = LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        List<String> keys = List.of(
                NotificationRedisKey.unreadInteraction(String.valueOf(userId)),
                NotificationRedisKey.unreadLike(String.valueOf(userId)),
                NotificationRedisKey.unreadFavorite(String.valueOf(userId))
        );
        List<String> values;
        try {
            values = redisService.mGet(keys);
        } catch (Exception e) {
            log.error("获取未读计数失败, userId={}", userId, e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }

        if (values == null) {
            return Response.ok(
                    NotificationUnreadCountRespVO.builder()
                            .interaction(0)
                            .like(0)
                            .favorite(0)
                            .build()
            );
        }
        //可能乱序
        return Response.ok(
                NotificationUnreadCountRespVO.builder()
                        .interaction(parse(values.get(0)))
                        .like(parse(values.get(1)))
                        .favorite(parse(values.get(2)))
                        .build()
        );
    }

    private Integer parse(String val) {
        if (val == null) {
            return 0;
        }
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }
}
