package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(itineraryProperties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(itineraryProperties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(itineraryProperties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix(Constant.THREAD_PREFIX);
        executor.initialize();
        return executor;
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService timeLimiterScheduler() {
        return Executors.newScheduledThreadPool(itineraryProperties.getAsync().getTimeLimitScheduler().getCorePoolSize(),
                                                r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName(Constant.TIME_LIMIT_SCHEDULER_PREFIX + t.getId());
            return t;
        });
    }

    @Bean
    public TimeLimiter timeLimiter(TimeLimiterRegistry timeLimiterRegistry) {
        return timeLimiterRegistry.timeLimiter(Constant.EXTERNAL_API_TIMEOUT_CONFIG);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            log.error("Exception in async method: {}", method.getName(), ex);
            throw new RuntimeException("Exception in async method: " + method.getName(), ex);
        };
    }
}
