package com.neu.youthpathtalk.user.biz.vo.rep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/14 11:36
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorWeeklyRankRespVO {
    private Long userId;
    private String username;
    private String userAvatar;
    private String universityName;
    private String type;       // 考公/考研/就业等
    private Double score;      // 当周总分
}
