package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.BoolEnum;
import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/03/21 16:54
 * @description 滑动分页请求VO
 */
@Data
@NoArgsConstructor
public class CursorPageReqVO {
    private BoolEnum lastIsTop;
    private BoolEnum lastIsEssence;
    private LocalDateTime lastCreateTime;
    private Long lastId;
    @NotNull(message = "分页大小不能为空")
    private PageSizeEnum size = PageSizeEnum.defaultSize();
}
