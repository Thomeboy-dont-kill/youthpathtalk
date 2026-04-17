package com.neu.youthpathtalk.post.biz.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/23 20:14
 * @description 点赞/取消点赞帖子给前端的响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeRespVO {
    private Boolean liked;
    private Long likeCount;
}
