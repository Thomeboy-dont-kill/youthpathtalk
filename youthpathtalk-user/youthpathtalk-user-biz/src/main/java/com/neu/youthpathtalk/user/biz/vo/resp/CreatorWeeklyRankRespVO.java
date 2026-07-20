package com.neu.youthpathtalk.user.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/14 11:36
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "创作者周榜项")
public class CreatorWeeklyRankRespVO {

    @Schema(
            description = "用户ID，用于跳转用户主页",
            example = "1"
    )
    private Long userId;

    @Schema(
            description = "用户名",
            example = "Julien"
    )
    private String username;

    @Schema(
            description = "用户头像地址",
            example = "/default-avatar.png"
    )
    private String userAvatar;

    @Schema(
            description = "用户所属学校名称",
            example = "东北大学"
    )
    private String universityName;

    @Schema(
            description = """
                    用户类型：
                    考研党
                    考公党
                    工作党
                    其他
                    """,
            example = "工作党"
    )
    private String type;       // 考公/考研/就业等

    @Schema(
            description = "最近一周累计热度分数，数值越大排名越高",
            example = "125.5"
    )
    private Double score;      // 当周总分
}
