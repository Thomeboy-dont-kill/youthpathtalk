package com.neu.youthpathtalk.post.biz.richtext.model.attrs;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Julien
 * @time 2026/07/03 11:24
 * @description
 */
@Data
public class ImageAttrs {
    private String url;
    private Integer width;
    private Integer height;

    public void check() {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("video url不能为空");
        }
        //暂时不做ImageAttrs业务强校验
    }
}
