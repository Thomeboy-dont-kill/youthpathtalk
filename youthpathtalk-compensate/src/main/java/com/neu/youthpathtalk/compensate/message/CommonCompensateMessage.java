package com.neu.youthpathtalk.compensate.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/04/07 11:12
 * @description 通用的补偿消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCompensateMessage {
    private Long userId;
    private Long postId;
    private Boolean add;//true=点赞/收藏,false=取消点赞/取消收藏
    private Long timestamp;
}
