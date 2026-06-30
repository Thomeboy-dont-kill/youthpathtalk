package com.neu.youthpathtalk.post.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/06/04 18:22
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "comment.hot-score")
public class CommentHotScoreProperties {

    private Integer recalculateDays = 7;

    private Integer batchSize = 500;

    private String cron = "0 0 3 * * ?";
}
