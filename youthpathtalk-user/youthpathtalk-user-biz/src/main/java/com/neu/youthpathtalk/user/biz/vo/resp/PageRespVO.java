package com.neu.youthpathtalk.user.biz.vo.resp;

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
public class PageRespVO<T> {
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
    private List<T> records;
}
