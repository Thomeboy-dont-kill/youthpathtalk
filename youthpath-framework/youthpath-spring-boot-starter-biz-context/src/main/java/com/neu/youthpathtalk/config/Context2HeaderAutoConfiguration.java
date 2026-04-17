package com.neu.youthpathtalk.config;

import com.neu.youthpathtalk.interceptor.FeignRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/03/04 21:44
 * @description
 */
@AutoConfiguration
public class Context2HeaderAutoConfiguration {
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor(){return new FeignRequestInterceptor();}
}
