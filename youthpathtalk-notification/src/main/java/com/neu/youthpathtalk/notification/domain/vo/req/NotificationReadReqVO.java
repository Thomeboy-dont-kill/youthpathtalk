package com.neu.youthpathtalk.notification.domain.vo.req;

import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/06/16 14:26
 * @description
 */
@Data
@Schema(description = "通知已读请求")
public class NotificationReadReqVO {

    @NotNull
    @Schema(
            description = "通知分类",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "INTERACTION"
    )
    private NotificationCategory category;
}