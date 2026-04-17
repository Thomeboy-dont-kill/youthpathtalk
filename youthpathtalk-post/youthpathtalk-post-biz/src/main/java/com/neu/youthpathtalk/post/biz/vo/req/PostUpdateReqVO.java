package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.enums.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/22 21:36
 * @description 更新帖子请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateReqVO {
    @NotNull(message = "帖子ID不能为空")
    private Long id;

    @NotNull(message = "板块类型不能为空")
    private BoardType boardType;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}
