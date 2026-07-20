package com.neu.youthpathtalk.distributed.id.generator.config;

import com.neu.youthpathtalk.distributed.id.generator.properties.LeafSpringBootProperties;
import com.neu.youthpathtalk.distributed.id.generator.leaf.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/03/07 14:28
 * @description 配置并把Bean(SegmentService.class,SnowflakeService.class)注入IOC容器
 */
@Configuration
@EnableConfigurationProperties(LeafSpringBootProperties.class)
public class LeafConfigure {
    private Logger logger = LoggerFactory.getLogger(LeafConfigure.class);
    @Autowired
    private LeafSpringBootProperties properties;

    @Bean
    public SegmentService initLeafSegmentStarter() throws Exception {
        if (properties != null && properties.getSegment() != null && properties.getSegment().isEnable()) {
            SegmentService segmentService = new SegmentService(properties.getSegment().getUrl(), properties.getSegment().getUsername(), properties.getSegment().getPassword());
            return segmentService;
        }
        logger.warn("init leaf segment ignore properties is {}", properties);
        return null;
    }
/*    @Bean
    public SnowflakeService initLeafSnowflakeStarter() throws InitException {
        if (properties != null && properties.getSnowflake() != null && properties.getSnowflake().isEnable()) {
            SnowflakeService snowflakeService = new SnowflakeService(properties.getSnowflake().getAddress(), properties.getSnowflake().getPort());
            return snowflakeService;
        }
        logger.warn("init leaf snowflake ignore properties is {}", properties);
        return null;
    }*/
}
