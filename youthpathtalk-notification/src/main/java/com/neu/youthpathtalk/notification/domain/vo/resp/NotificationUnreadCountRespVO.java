package com.neu.youthpathtalk.notification.domain.vo.resp;

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
public class NotificationUnreadCountRespVO {

    private Integer interaction;
    private Integer like;
    private Integer favorite;
    //后续新增交友模块再补充
//    private Integer follow;
}
