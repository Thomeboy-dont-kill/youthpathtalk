package com.neu.youthpathtalk.notification.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.notification.domain.service.NotificationService;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationListReqVO;
import com.neu.youthpathtalk.notification.domain.vo.req.NotificationReadReqVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.CursorPageRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationRespVO;
import com.neu.youthpathtalk.notification.domain.vo.resp.NotificationUnreadCountRespVO;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "通知模块",
        description = "用户通知相关接口"
)
public class NotificationController {
    private final NotificationService notificationService;

    @SaCheckLogin
    @PostMapping("/read")
    @Operation(
            summary = "标记某类通知全部已读",
            description = "将指定分类的通知全部标记为已读"
    )
    public Response<Void> read(@Valid @RequestBody NotificationReadReqVO req) {
        return notificationService.read(req);
    }

    @SaCheckLogin
    @PostMapping("/list")
    @Operation(
            summary = "分页查询通知列表",
            description = """
                    游标分页查询通知列表。
                    首次查询 cursor 传 null。
                    后续请求传入上一页返回的 cursor。
                    """
    )
    public Response<CursorPageRespVO<NotificationRespVO, Long>> list(@Valid @RequestBody NotificationListReqVO req){
        return notificationService.list(req);
    }

    @GetMapping("/unread/status")
    @Operation(
            summary = "查询是否存在未读通知",
            description = "返回当前用户是否存在任意未读通知"
    )
    public Response<Boolean> hasUnread() {
        return notificationService.hasUnread();
    }

    @SaCheckLogin
    @GetMapping("/unread/count")
    @Operation(
            summary = "查询各分类未读数量",
            description = "返回当前用户各通知分类的未读数量"
    )
    public Response<NotificationUnreadCountRespVO> getUnreadCount() {
        return notificationService.getUnreadCount();
    }
}
