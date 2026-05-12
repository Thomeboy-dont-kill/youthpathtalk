package com.neu.youthpathtalk.config;

import com.neu.youthpathtalk.interceptor.SearchInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Julien
 * @time 2026/05/12 16:51
 * @description
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final SearchInterceptor searchInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(searchInterceptor)
                .addPathPatterns("/search/**");
    }
}
