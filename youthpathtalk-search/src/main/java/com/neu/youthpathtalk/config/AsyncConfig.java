package com.neu.youthpathtalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Julien
 * @time 2026/05/27 13:53
 * @description
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("searchHistoryExecutor")
    public Executor searchHistoryExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);

        executor.setMaxPoolSize(4);

        executor.setQueueCapacity(1000);

        executor.setThreadNamePrefix("search-history-");

        executor.initialize();

        return executor;
    }
}