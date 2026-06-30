package com.neu.youthpathtalk.post.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/06/06 16:25
 * @description
 */
@Component
@ConfigurationProperties(prefix = "comment.conversation")
@Data
public class CommentConversationProperties {

    /**
     * 最大递归深度
     */
    private Integer maxDepth = 10;

    /**
     * 最大返回节点数
     */
    private Integer maxNodeCount = 200;
}
