package com.neu.youthpathtalk.config;

import com.neu.youthpathtalk.aspect.ApiOperationLogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julien
 * @time 2026/03/05 10:42
 * @description 这是一个记录API操作日志的自动配置类
 */
@AutoConfiguration
public class OperationLogAutoConfiguration {
    @Bean
    public ApiOperationLogAspect apiOperationLogAspect(){return new ApiOperationLogAspect();}
}
