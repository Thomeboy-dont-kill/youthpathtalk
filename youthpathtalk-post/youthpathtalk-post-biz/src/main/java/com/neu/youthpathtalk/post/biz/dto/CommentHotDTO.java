package com.neu.youthpathtalk.post.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/09 15:11
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentHotDTO {
    private Long id;
    private String userName;
    private String content;
}
