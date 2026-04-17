package com.neu.youthpathtalk.user.biz.vo.rep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/06 15:12
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrowseHistoryVO {
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
    private LocalDateTime browseTime;
}
