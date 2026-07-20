package com.neu.youthpathtalk.vo.req;

import com.neu.youthpathtalk.enums.BoardType;
import com.neu.youthpathtalk.enums.PageSizeEnum;
import com.neu.youthpathtalk.enums.PublishTimeRange;
import com.neu.youthpathtalk.enums.SearchSortType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/10 12:52
 * @description
 */
@Data
@NoArgsConstructor
@Schema(description = "帖子搜索请求参数")
public class SearchPostsReqVO {
    @Schema(
            description = "搜索关键词，不能为空白字符串",
            example = "大三怎么找实习",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "关键词不能为空")
    private String keyword;

    @Schema(
            description = """
                    板块筛选：

                    GRAD = 考研

                    CIVIL = 考公

                    WORK = 工作
                    """,
            example = "WORK"
    )
    private BoardType boardType;

    @Schema(
            description = """
                    发布时间筛选：

                    ONE_DAY = 最近一天

                    ONE_WEEK = 最近一周

                    ONE_MONTH = 最近一个月

                    OLDER = 更早

                    当 startTime 或 endTime 不为空时，
                    本参数将被忽略。
                    """,
            example = "ONE_WEEK"
    )
    private PublishTimeRange publishTimeRange;

    @Schema(
            description = """
                    自定义发布时间区间开始时间。

                    毫秒时间戳。

                    指定后 publishTimeRange 将失效。
                    """,
            example = "1743264000000"
    )
    private Long startTime;

    @Schema(
            description = """
                    自定义发布时间区间结束时间。

                    毫秒时间戳。

                    指定后 publishTimeRange 将失效。
                    """,
            example = "1745856000000"
    )
    private Long endTime;

    @Schema(
            description = """
                    排序方式：

                    RELEVANCE = 综合排序（默认）

                    CREATE_TIME = 最新发布

                    VIEW_COUNT = 最热
                    """,
            example = "RELEVANCE"
    )
    private SearchSortType sortType = SearchSortType.RELEVANCE;

    //用游标分页
    @ArraySchema(
            schema = @Schema(
                    description = """
                            游标分页参数。

                            首次搜索不传。

                            后续分页必须原样传递上一页返回的
                            searchAfter 值。

                            禁止客户端修改。
                            """
            )
    )
    private List<Object> searchAfter;

    @Schema(
            description = """
                    分页大小。

                    支持：

                    SIZE_10 = 10条

                    SIZE_20 = 20条

                    SIZE_30 = 30条

                    SIZE_50 = 50条
                    """,
            example = "SIZE_10"
    )
    @NotNull(message = "分页大小不能为空")
    private PageSizeEnum size = PageSizeEnum.defaultSize();
}
