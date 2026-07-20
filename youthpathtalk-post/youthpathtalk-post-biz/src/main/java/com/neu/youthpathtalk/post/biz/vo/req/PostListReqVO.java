package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.BoolEnum;
import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "帖子列表分页请求参数")
public class PostListReqVO {
    @Schema(
            description = """
                    上一页最后一条帖子是否为置顶帖。

                    首次加载不传；
                    加载下一页时必须传入上一页最后一条记录的该值。
                    """,
            implementation = BoolEnum.class
    )
    private BoolEnum lastIsTop;

    @Schema(
            description = """
                    上一页最后一条帖子是否为精华帖。

                    首次加载不传；
                    加载下一页时必须传入上一页最后一条记录的该值。
                    """,
            implementation = BoolEnum.class
    )
    private BoolEnum lastIsEssence;

    @Schema(
            description = """
                    上一页最后一条帖子的创建时间。

                    首次加载不传；
                    加载下一页时必须传入上一页最后一条记录的该值。
                    """,
            example = "2026-04-24T10:30:30"
    )
    private LocalDateTime lastCreateTime;

    @Schema(
            description = """
                    上一页最后一条帖子的ID。

                    首次加载不传；
                    加载下一页时必须传入上一页最后一条记录的该值。
                    """,
            example = "2001"
    )
    private Long lastId;

    @NotNull(message = "分页大小不能为空")
    @Schema(
            description = """
                    分页大小。

                    可选值：
                    - SIZE_10：10条
                    - SIZE_20：20条
                    - SIZE_30：30条
                    - SIZE_50：50条

                    默认 SIZE_10。
                    """,
            implementation = PageSizeEnum.class
    )
    private PageSizeEnum size = PageSizeEnum.defaultSize();
}
