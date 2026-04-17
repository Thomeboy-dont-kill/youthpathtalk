package com.neu.youthpathtalk.config;

import com.neu.youthpathtalk.filter.HeaderUserId2ContextFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/03/04 21:43
 * @description
 */
@AutoConfiguration
public class Header2ContextAutoConfiguration {
    @Bean
    public FilterRegistrationBean<HeaderUserId2ContextFilter> filterFilterRegistrationBean(){
        HeaderUserId2ContextFilter headerUserId2ContextFilter=new HeaderUserId2ContextFilter();
        return new FilterRegistrationBean<>(headerUserId2ContextFilter);
    }
}
