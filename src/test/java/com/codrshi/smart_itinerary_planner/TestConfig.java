package com.codrshi.smart_itinerary_planner;

import com.codrshi.smart_itinerary_planner.common.aspect.AttractionCacheAspect;
import com.codrshi.smart_itinerary_planner.common.aspect.AuxiliaryCacheAspect;
import com.codrshi.smart_itinerary_planner.common.aspect.GetItineraryCacheAspect;
import com.codrshi.smart_itinerary_planner.common.aspect.PatchItineraryCacheAspect;
import com.codrshi.smart_itinerary_planner.common.aspect.WeatherCacheAspect;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.DeleteResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.UpdateNotePatchHandler;
import lombok.Getter;
import lombok.Setter;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
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

    @Bean
    public GetItineraryCacheAspect getItineraryCacheAspect() {
        return null;
    }

    @Bean
    public AttractionCacheAspect attractionCacheAspect() {
        return null;
    }

    @Bean
    public WeatherCacheAspect weatherCacheAspect() {
        return null;
    }

    @Bean
    public AuxiliaryCacheAspect auxiliaryCacheAspect() {
        return null;
    }

//    @Bean
//    public DeleteItineraryCacheAspect deleteItineraryCacheAspect() {
//        return null;
//    }

    @Bean
    public PatchItineraryCacheAspect patchItineraryCacheAspect() {
        return null;
    }

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
