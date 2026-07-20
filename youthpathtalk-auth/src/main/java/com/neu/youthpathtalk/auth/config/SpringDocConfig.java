package com.neu.youthpathtalk.auth.config;

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
    public OpenAPI authOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("YouthPathTalk Auth API")
                                .description("""
                                    认证说明：

                                    本系统采用 Cookie 认证方案。

                                    登录成功后：

                                    服务端会自动通过 Set-Cookie 下发认证 Cookie。

                                    前端无需保存 Token。

                                    浏览器会自动携带 Cookie 完成后续认证。
                                    """)
                                .version("1.0")
                );
    }
}
