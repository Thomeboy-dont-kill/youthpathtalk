package com.neu.youthpathtalk.post.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/06/03 21:18
 * @description
 */
@Data
@NoArgsConstructor
public class ReplyTargetDTO {
    //private Long parentId;//后面再根据实际需求来决定是否拓展字段
    private Long postId;

    private Long userId;

    private String userName;

    private Long rootId;

    private String plainText;
}
