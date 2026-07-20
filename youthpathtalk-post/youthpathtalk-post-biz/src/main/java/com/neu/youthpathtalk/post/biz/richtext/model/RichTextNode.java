package com.neu.youthpathtalk.post.biz.richtext.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.youthpathtalk.post.biz.enums.RichTextNodeType;
import com.neu.youthpathtalk.post.biz.richtext.model.attrs.MentionAttrs;
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
    private RichTextNodeType type;

    private String text;

    private JsonNode attrs;

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

            case MENTION, IMAGE, VIDEO -> {
                if (attrs == null) {
                    throw new IllegalArgumentException(type + " 节点attrs不能为空");
                }
                if (StringUtils.isNotBlank(text)) {
                    throw new IllegalArgumentException(type + "节点不能包含text");
                }
            }
        }
    }
}
