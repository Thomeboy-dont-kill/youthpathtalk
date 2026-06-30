package com.neu.youthpathtalk.notification.domain.service;

import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationListReqVO;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationReadReqVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.CursorPageRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationUnreadCountRespVO;
import com.neu.youthpathtalk.response.Response;

/**
 * @author Julien
 * @time 2026/06/12 17:56
 * @description
 */
public interface NotificationService {
    Response<CursorPageRespVO<NotificationRespVO,Long>> list(NotificationListReqVO req);
    Response<Boolean> hasUnread();
    Response<NotificationUnreadCountRespVO> getUnreadCount();
    Response<Void> read(NotificationReadReqVO req);
}
