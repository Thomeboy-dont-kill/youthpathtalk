package com.neu.youthpathtalk.notification.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.notification.domain.service.NotificationService;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationListReqVO;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationReadReqVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.CursorPageRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationUnreadCountRespVO;
import com.neu.youthpathtalk.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Julien
 * @time 2026/06/15 16:53
 * @description
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @SaCheckLogin
    @PostMapping("/read")
    public Response<Void> read(@Valid @RequestBody NotificationReadReqVO req) {
        return notificationService.read(req);
    }

    @SaCheckLogin
    @PostMapping("/list")
    public Response<CursorPageRespVO<NotificationRespVO, Long>> list(@Valid @RequestBody NotificationListReqVO req){
        return notificationService.list(req);
    }

    @GetMapping("/unread/status")
    public Response<Boolean> hasUnread() {
        return notificationService.hasUnread();
    }

    @SaCheckLogin
    @GetMapping("/unread/count")
    public Response<NotificationUnreadCountRespVO> getUnreadCount() {
        return notificationService.getUnreadCount();
    }
}
