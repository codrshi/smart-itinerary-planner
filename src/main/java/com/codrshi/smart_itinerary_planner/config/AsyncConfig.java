package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
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

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            System.err.println("Exception in async method: "+ method.getName());
            ex.printStackTrace();
        };
    }
}
