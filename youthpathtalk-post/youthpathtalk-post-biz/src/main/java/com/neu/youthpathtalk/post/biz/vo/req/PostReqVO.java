package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 11:31
 * @description 发布帖子VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReqVO {
    /**
     * 板块类型：0-考研，1-考公，2-工作
     */
    @NotNull(message = "板块类型不能为空")
    private BoardType boardType;

    /**
     * 帖子标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
}
