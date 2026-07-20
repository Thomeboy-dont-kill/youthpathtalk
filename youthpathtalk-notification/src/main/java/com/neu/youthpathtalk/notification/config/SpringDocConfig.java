package com.neu.youthpathtalk.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/06/30 13:41
 * @description
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI notificationOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("YouthPathTalk Notification API")
                                .description("通知模块接口文档")
                                .version("1.0")
                );
    }
}
