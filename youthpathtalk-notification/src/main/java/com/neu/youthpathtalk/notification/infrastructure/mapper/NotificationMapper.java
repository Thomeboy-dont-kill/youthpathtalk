package com.neu.youthpathtalk.notification.infrastructure.mapper;

import com.neu.youthpathtalk.notification.domain.entity.NotificationDO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/13 9:57
 * @description
 */
@Mapper
public interface NotificationMapper {
    int insert(NotificationDO notificationDO);
    List<NotificationRespVO> selectByReceiverIdAndTypes(
            @Param("receiverId") Long receiverId,
            @Param("types") List<Integer> types,
            @Param("cursor") Long cursor,
            @Param("limit") Integer limit
    );
    int updateReadByReceiverIdAndTypes(
            @Param("userId") Long userId,
            @Param("types") List<Integer> types
    );
}
