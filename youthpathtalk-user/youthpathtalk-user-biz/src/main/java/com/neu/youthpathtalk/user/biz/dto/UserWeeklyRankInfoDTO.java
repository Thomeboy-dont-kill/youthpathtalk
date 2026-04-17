package com.neu.youthpathtalk.user.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/14 12:52
 * @description 用于创作者周榜展示的DTO
 */
@Data
@NoArgsConstructor
public class UserWeeklyRankInfoDTO {
    private Long id;
    private String username;
    private String avatar;
    private Long universityId;
    private Integer type;       // 考公/考研/就业等
}
