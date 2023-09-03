package com.thinkstu.config;

import org.springframework.context.annotation.*;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService threadPoolExecutor() {
        // 2个核心线程，8个最大线程，500个任务队列，CallerRunsPolicy拒绝策略
        return new ThreadPoolExecutor(
                2, 8,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(500),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
