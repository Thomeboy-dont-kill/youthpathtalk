package com.neu.youthpathtalk.post.biz.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/04 21:19
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRespVO {

    private Long id;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Long universityId;

    private String universityName;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private LocalDateTime createTime;

    private BigDecimal hotScore;
}
