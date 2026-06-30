package com.neu.youthpathtalk.post.biz.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/05 11:27
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyCommentRespVO {
    private Long id;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Long universityId;

    private String universityName;

    private Long rootId;

    private Long parentId;

    private Long replyUserId;

    private String replyUserName;

    private Boolean showReplyUser=Boolean.TRUE;

    private String content;

    private Integer likeCount;

    private Integer status;

    private LocalDateTime createTime;
}
