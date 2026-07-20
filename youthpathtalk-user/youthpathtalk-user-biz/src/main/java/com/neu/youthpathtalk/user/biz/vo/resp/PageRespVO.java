package com.neu.youthpathtalk.user.biz.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/04/07 21:25
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应对象")
public class PageRespVO<T> {
    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;

    @Schema(description = "当前页数据列表")
    private List<T> records;
}
