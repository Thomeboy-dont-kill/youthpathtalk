package com.neu.youthpathtalk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/05/11 17:15
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "canal")
public class CanalProperties {
    private String host;

    private Integer port;

    private String destination;

    private String username;

    private String password;

    private String subscribe;

    private Integer batchSize = 100;

    private Integer getTimeout = 5;
}
