package com.neu.youthpathtalk.post.biz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/06/12 9:59
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDO {

    private Long id;

    private Long receiverId;

    private Long senderId;

    private String senderName;

    private String senderAvatar;

    private Integer type;

    private Integer targetType;

    private Long targetId;

    private String targetTitle;

    private String targetContent;

    private String contentPreview;

    private Integer isRead;

    private LocalDateTime createTime;
}
