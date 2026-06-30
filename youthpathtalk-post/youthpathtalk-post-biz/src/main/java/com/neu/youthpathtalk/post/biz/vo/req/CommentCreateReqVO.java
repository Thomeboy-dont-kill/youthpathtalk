package com.neu.youthpathtalk.post.biz.vo.req;

import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/02 23:11
 * @description
 */
@Data
@NoArgsConstructor
public class CommentCreateReqVO {

    @NotNull
    private Long postId;

    @NotNull
    private RichTextDoc content;
}
