package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.util.AttractionLimitCalculator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.AttractionRedisKeyGenerator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.WeatherRedisKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class AttractionCacheAspect {
    @Autowired
    private RedisTemplate<String, List<IAttractionDTO>> redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Around(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.ExternalApiService.getOpenStreetMapAttractions(..))", argNames = "coordinateDTO, totalDays")
    public Object cacheAttraction(ProceedingJoinPoint joinPoint, ICoordinateDTO coordinateDTO, int totalDays) {

        int limit = getLimit(totalDays);
        String redisKey = AttractionRedisKeyGenerator.generate(coordinateDTO);

        List<IAttractionDTO> cachedAttractions = getFromCache(redisKey);

        if(cachedAttractions != null && limit <= cachedAttractions.size()) {
            log.debug("CACHE HIT: attractions found for coordinateDTO = {} is within the limit: {}", coordinateDTO,
                      limit);
            return cachedAttractions.stream().limit(limit).collect(Collectors.toList());
        }

        log.debug("CACHE MISS: attractions not found for coordinateDTO = {} or limit: {} exceeds number of cached " +
                          "attractions = {}", coordinateDTO, limit, cachedAttractions.size());
        List<IAttractionDTO> attractions;

        try {
            attractions  = (List<IAttractionDTO>) joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        redisTemplate.opsForValue().set(redisKey, attractions, Duration.ofDays(itineraryProperties.getRedis().getAttractionTtl()));

        return attractions;
    }

    private int getLimit(int totalDays) {
        ItineraryProperties.AttractionProperties attractionProperties = itineraryProperties.getAttraction();
        int limit = AttractionLimitCalculator.calculate(totalDays, attractionProperties.getBase(), attractionProperties.getScale(), attractionProperties.getMaxLimit());
        return limit;
    }

    private List<IAttractionDTO> getFromCache(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey);
    }
}
