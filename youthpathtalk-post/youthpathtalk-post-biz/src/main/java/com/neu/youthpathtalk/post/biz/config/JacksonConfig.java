package com.neu.youthpathtalk.post.biz.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/03/22 13:47
 * @description 对全局的objectMapper的配置
 */
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer dateFormatCustomizer() {
        return builder -> {
        };
    }
}
