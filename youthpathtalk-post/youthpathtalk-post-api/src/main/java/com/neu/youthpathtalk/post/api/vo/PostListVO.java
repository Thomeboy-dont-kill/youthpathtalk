package com.neu.youthpathtalk.post.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/03/21 16:36
 * @description 用于分页展示的PostListVO
 */
@Data
@NoArgsConstructor
//支持链式编程，灵活修改
@Accessors(chain = true)
public class PostListVO {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private String universityName;
    private Integer boardType;
    private String boardTypeName;
    private String title;
    private String preview;           // 内容预览（前50字）
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer isTop;
    private Integer isEssence;
    private LocalDateTime updateTime;
}
