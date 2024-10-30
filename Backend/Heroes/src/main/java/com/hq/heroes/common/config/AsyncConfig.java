package com.hq.heroes.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50); // 기본 스레드 개수 설정 (50~100 권장)
        executor.setMaxPoolSize(100); // 최대 100개의 스레드 허용
        executor.setQueueCapacity(0); // 대기 없이 모든 요청을 처리하도록 설정
        executor.setThreadNamePrefix("Notification-Async-");
        executor.initialize();
        return executor;
    }
}

