package com.codrshi.smart_itinerary_planner;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.service.DeletePatchHandlerTest;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.DeleteResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.UpdateNotePatchHandler;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@TestConfiguration
public class TestConfig {

    @Bean
    public MoveResourcePatchHandler movePatchHandler() {
        return new MoveResourcePatchHandler();
    }

    @Bean
    public DeleteResourcePatchHandler deletePatchHandler() {
        return new DeleteResourcePatchHandler();
    }

    @Bean
    public UpdateNotePatchHandler updatePatchHandler() {
        return new UpdateNotePatchHandler();
    }

    @Bean
    public RedisCacheManager cacheManager() {
        return Mockito.mock(RedisCacheManager.class);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }

    @Bean
    @Primary
    public Executor taskExecutor() {
        return Runnable::run; // synchronous executor for tests
    }

//    @Bean
//    @RequestScope
//    public CounterManager counterManager() {
//        return new CounterManager();
//    }

    @Getter
    @Setter
    public static class PageWrapper<T, R> {
        private List<R> content;
        private PageableImpl pageable;
        private long totalElements;

        public Page<T> toPage() {
            List<T> content = this.content.stream().map(item -> (T) item).collect(Collectors.toList());

            Pageable pageable = PageRequest.of(this.pageable.pageNumber, this.pageable.pageSize);
            return new PageImpl<>(content, pageable, totalElements);
        }
    }

    @Getter
    @Setter
    public static class PageableImpl {
        private int pageNumber;
        private int pageSize;
    }
}
