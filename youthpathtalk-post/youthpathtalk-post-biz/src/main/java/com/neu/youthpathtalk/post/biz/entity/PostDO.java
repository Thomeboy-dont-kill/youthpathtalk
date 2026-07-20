package com.neu.youthpathtalk.post.biz.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
public class PostDO {
    private Long id;

    private Long userId;

    private String username;

    private String userAvatar;

    private Long universityId;

    private String universityName;

    private Integer boardType;

    private String title;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer favoriteCount;

    private Integer isTop;

    private Integer isEssence;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String content;

    private String plainText;

    private LocalDateTime topEndTime;

    private LocalDateTime essenceEndTime;
}