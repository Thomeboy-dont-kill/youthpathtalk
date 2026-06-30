package com.neu.youthpathtalk.notification.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 17:02
 * @description 滑动分页响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageRespVO<T,C> {
    private List<T> list;
    private Boolean hasNext;
    private C cursor;
}
