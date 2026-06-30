package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/06 18:10
 * @description
 */
@Data
@NoArgsConstructor
public class CommentEditDTO {

    private Long id;

    private Long userId;

    private String content;

    private LocalDateTime createTime;
}
