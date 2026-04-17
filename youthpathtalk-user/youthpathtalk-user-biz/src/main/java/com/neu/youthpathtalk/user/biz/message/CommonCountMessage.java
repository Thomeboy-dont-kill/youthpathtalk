package com.neu.youthpathtalk.user.biz.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/24 19:46
 * @description 通用的计数消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonCountMessage {
    private String id;
    private Long targetId;
    private Long delta;
}
