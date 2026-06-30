package com.neu.youthpathtalk.post.biz.vo.cursor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/04 22:10
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCursor {

    private BigDecimal hotScore;

    private Long id;
}