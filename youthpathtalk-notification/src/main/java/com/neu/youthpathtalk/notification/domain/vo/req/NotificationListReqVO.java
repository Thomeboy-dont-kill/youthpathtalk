package com.neu.youthpathtalk.notification.domain.vo.req;

import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.common.enums.PageSizeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/06/15 16:06
 * @description
 */
@Data
public class NotificationListReqVO {
    @NotNull
    private NotificationCategory category;

    private Long cursor;

    private PageSizeEnum limit=PageSizeEnum.defaultSize();
}
