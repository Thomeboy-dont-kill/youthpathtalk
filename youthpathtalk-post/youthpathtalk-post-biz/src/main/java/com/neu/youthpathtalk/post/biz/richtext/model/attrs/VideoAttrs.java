package com.neu.youthpathtalk.post.biz.richtext.model.attrs;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Julien
 * @time 2026/07/03 11:24
 * @description
 */
@Data
public class VideoAttrs {
    private String url;
    private Integer duration;

    public void check() {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("video url不能为空");
        }
        //暂时不做VideoAttrs业务强校验
    }
}
