package com.neu.youthpathtalk.post.biz.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Julien
 * @time 2026/04/10 16:43
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeCountDecrMessage {
    private String id;
    private Map<Long, Long> userDeltas;
}
