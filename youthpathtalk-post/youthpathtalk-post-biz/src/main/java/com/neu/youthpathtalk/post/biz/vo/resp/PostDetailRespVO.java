package com.neu.youthpathtalk.post.biz.vo.resp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Julien
 * @time 2026/03/21 20:54
 * @description 查看帖子详情响应VO
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
public class PostDetailRespVO {
    private Long id;

    private Long userId;

    private String username;

    private String userAvatar;

    private String universityName;

    private Integer boardType;

    private String boardTypeName;

    private String title;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer favoriteCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String content;

    private Boolean liked;

    private Boolean favorited;
}