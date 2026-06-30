package com.neu.youthpathtalk.post.biz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/03 14:09
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDO {

    private Long id;

    private Long postId;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Long universityId;

    private String universityName;

    private Long rootId;

    private Long parentId;

    private Long replyUserId;

    private String replyUserName;

    private String content;

    private String plainText;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
