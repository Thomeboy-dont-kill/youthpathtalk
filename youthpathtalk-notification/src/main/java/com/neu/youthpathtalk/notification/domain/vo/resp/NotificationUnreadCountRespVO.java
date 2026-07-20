package com.neu.youthpathtalk.notification.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/16 13:32
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "未读通知数量统计")
public class NotificationUnreadCountRespVO {

    @Schema(
            description = "互动类通知未读数量（评论、回复、@）",
            example = "3"
    )
    private Integer interaction;
    @Schema(
            description = "点赞通知未读数量",
            example = "10"
    )
    private Integer like;
    @Schema(
            description = "收藏通知未读数量",
            example = "2"
    )
    private Integer favorite;
    //后续新增交友模块再补充
//    private Integer follow;
}
