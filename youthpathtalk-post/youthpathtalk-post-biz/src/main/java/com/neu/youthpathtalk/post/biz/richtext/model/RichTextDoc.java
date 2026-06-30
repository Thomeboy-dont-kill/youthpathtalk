package com.neu.youthpathtalk.post.biz.richtext.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/23 10:48
 * @description
 */
@Data
public class RichTextDoc {

    private String type = "doc"; // doc

    private List<RichTextNode> content;

    public void check() {

        if (CollectionUtils.isEmpty(content)) {
            throw new IllegalArgumentException("富文本内容不能为空");
        }

        for (RichTextNode node : content) {
            node.check();
        }
    }
}