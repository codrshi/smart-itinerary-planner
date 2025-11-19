package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.codrshi.smart_itinerary_planner.util.patch.PatchRequestDeserializer;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@EnableConfigurationProperties(ItineraryProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

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
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(RequestContext.getCurrentContext().getUsername());
    }

    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer serializer) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer((new StringRedisSerializer()));
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer serializer,
                                          ItineraryProperties itineraryProperties) {
        ItineraryProperties.RedisProperties redisProperties = itineraryProperties.getRedis();
        Map<String, RedisCacheConfiguration>  cacheConfigurationMap = new HashMap<>();

        cacheConfigurationMap.put(Constant.COORDINATE_CACHE,
                                  defaultCacheConfig(serializer).entryTtl(Duration.ofDays(redisProperties.getCoordinateTtl())));
        cacheConfigurationMap.put(Constant.EVENT_CACHE,
                                  defaultCacheConfig(serializer).entryTtl(Duration.ofDays(redisProperties.getEventTtl())));

        return RedisCacheManager.builder(redisConnectionFactory).withInitialCacheConfigurations(cacheConfigurationMap).build();
    }

    private RedisCacheConfiguration defaultCacheConfig(GenericJackson2JsonRedisSerializer serializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
