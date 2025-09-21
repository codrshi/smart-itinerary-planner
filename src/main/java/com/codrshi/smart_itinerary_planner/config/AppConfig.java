package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.codrshi.smart_itinerary_planner.util.PatchRequestDeserializer;
import com.codrshi.smart_itinerary_planner.util.PatchDataRegistry;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.ICoordinateMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryHistoryMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IWeatherMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.implementation.AttractionMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.implementation.CoordinateMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.implementation.EventMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.implementation.ItineraryHistoryMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.implementation.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Executor;

@Configuration
@EnableMongoAuditing
@EnableAsync
@EnableConfigurationProperties(ItineraryProperties.class)
public class AppConfig {

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Bean
    public IItineraryHistoryMapper itineraryHistoryMapper() {
        return new ItineraryHistoryMapper();
    }

    @Bean
    public IEventMapper eventMapper() {
        return new EventMapper();
    }

    @Bean
    public ICoordinateMapper coordinateMapper() {
        return new CoordinateMapper();
    }

    @Bean
    public IAttractionMapper attractionMapper() {
        return new AttractionMapper();
    }

    @Bean
    public IWeatherMapper weatherMapper() {
        return new WeatherMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public LocationUtil locationUtil() {
        return new LocationUtil();
    }

    @Bean
    public FactoryUtil factoryUtil() {
        return new FactoryUtil();
    }

    @Bean
    public ActivityUtil activityUtil() {
        return new ActivityUtil();
    }

    @Bean
    public CounterManager counterManager() {
        return new CounterManager();
    }

    @Bean
    public PatchRequestDeserializer patchDataDeserializer() {
        return new PatchRequestDeserializer();
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        //fetch logged-in user from spring SecurityContext
        return () -> Optional.of("system_user");
    }

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
}
