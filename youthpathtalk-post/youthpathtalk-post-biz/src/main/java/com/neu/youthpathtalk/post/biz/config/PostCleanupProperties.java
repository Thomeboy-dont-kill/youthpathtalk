package com.neu.youthpathtalk.post.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/03/23 14:05
 * @description 从配置文件加载属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "post.cleanup")
public class PostCleanupProperties {
    private boolean enabled = true;
    private int retainDays = 30;
    private int batchSize = 1000;
    private String cron = "0 0 3 * * ?";
}
