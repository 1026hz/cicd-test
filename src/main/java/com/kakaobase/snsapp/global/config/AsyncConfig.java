package com.kakaobase.snsapp.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 *
 * <p>Spring의 @Async 어노테이션을 위한 설정입니다.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 비동기 작업을 위한 ThreadPoolTaskExecutor 빈 생성
     *
     * @return 설정된 TaskExecutor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 코어 스레드 개수
        executor.setCorePoolSize(2);

        // 최대 스레드 개수
        executor.setMaxPoolSize(10);

        // 큐 용량
        executor.setQueueCapacity(500);

        // 스레드 이름 prefix
        executor.setThreadNamePrefix("sns-async-");

        // 초기화
        executor.initialize();

        return executor;
    }
}