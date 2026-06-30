package com.neu.youthpathtalk.post.biz.richtext.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neu.youthpathtalk.post.biz.enums.RichTextNodeType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Julien
 * @time 2026/06/23 10:48
 * @description
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RichTextNode {

    private RichTextNodeType type; // text / mention

    private String text; // text node

    private MentionAttrs attrs; // mention node

    public void check() {

        if (type == null) {
            throw new IllegalArgumentException("节点类型不能为空");
        }

        switch (type) {

            case TEXT -> {

                if (StringUtils.isBlank(text)) {
                    throw new IllegalArgumentException("文本节点不能为空");
                }

                if (attrs != null) {
                    throw new IllegalArgumentException("文本节点不能包含attrs");
                }
            }

            case MENTION -> {

                if (attrs == null) {
                    throw new IllegalArgumentException("mention节点属性不能为空");
                }

                if (StringUtils.isNotBlank(text)) {
                    throw new IllegalArgumentException("mention节点不能包含text");
                }

                attrs.check();
            }
        }
    }
}
