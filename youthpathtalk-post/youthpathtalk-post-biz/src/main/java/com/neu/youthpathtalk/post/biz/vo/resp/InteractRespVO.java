package com.neu.youthpathtalk.post.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/23 20:14
 * @description 互动/取消互动给前端的响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "互动响应")
public class InteractRespVO {

    @Schema(
            description = "当前用户是否已经互动",
            example = "true"
    )
    private Boolean interacted;
    @Schema(
            description = "当前互动数量",
            example = "128"
    )
    private Long count;
}
