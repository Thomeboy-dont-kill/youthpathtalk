package com.neu.youthpathtalk.notification.domain.vo.req;

import com.neu.youthpathtalk.notification.common.enums.NotificationCategory;
import com.neu.youthpathtalk.notification.common.enums.PageSizeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Julien
 * @time 2026/06/15 16:06
 * @description
 */
@Data
@Schema(description = "通知列表查询请求")
public class NotificationListReqVO {
    @NotNull
    @Schema(
            description = """
                    通知分类：

                    INTERACTION = 回复与@

                    LIKE = 收到的赞

                    FAVORITE = 收藏

                    FOLLOW = 新增粉丝
                    """,
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "INTERACTION"
    )
    private NotificationCategory category;

    @Schema(
            description = """
                    游标值。
                    首次查询传 null。
                    后续查询传上一页返回的 cursor。
                    """,
            example = "10"
    )
    private Long cursor;

    @Schema(
            description = """
                    分页大小（默认10）。

                    支持：

                    SIZE_10 = 10条

                    SIZE_20 = 20条

                    SIZE_30 = 30条
                    """,
            example = "SIZE_10"
    )
    private PageSizeEnum limit=PageSizeEnum.defaultSize();
}
