package com.neu.youthpathtalk.notification.domain.vo.req;

import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/06/16 14:26
 * @description
 */
@Data
public class NotificationReadReqVO {

    @NotNull
    private NotificationCategory category;
}