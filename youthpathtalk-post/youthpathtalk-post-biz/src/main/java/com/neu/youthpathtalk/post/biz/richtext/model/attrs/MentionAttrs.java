package com.neu.youthpathtalk.post.biz.richtext.model.attrs;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Julien
 * @time 2026/06/23 10:49
 * @description
 */
@Data
public class MentionAttrs {
    private Long userId;
    private String username;

    public void check() {

        if (userId == null) {
            throw new IllegalArgumentException("mention userId不能为空");
        }

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("mention username不能为空");
        }
    }
}