package com.neu.youthpathtalk.post.biz.vo.cursor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/05 11:42
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeIdCursor {
    private LocalDateTime createTime;
    private Long id;
}
