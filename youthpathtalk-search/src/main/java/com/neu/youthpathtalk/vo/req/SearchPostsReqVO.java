package com.neu.youthpathtalk.vo.req;

import com.neu.youthpathtalk.enums.BoardType;
import com.neu.youthpathtalk.enums.PageSizeEnum;
import com.neu.youthpathtalk.enums.PublishTimeRange;
import com.neu.youthpathtalk.enums.SearchSortType;
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
public class SearchPostsReqVO {
    @NotBlank(message = "关键词不能为空")
    private String keyword;
    private BoardType boardType;
    private PublishTimeRange publishTimeRange;
    private Long startTime;
    private Long endTime;
    private SearchSortType sortType = SearchSortType.RELEVANCE;
    //用游标分页
    private List<Object> searchAfter;
    @NotNull(message = "分页大小不能为空")
    private PageSizeEnum size = PageSizeEnum.defaultSize();
}
