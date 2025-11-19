package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.codrshi.smart_itinerary_planner.util.AttractionLimitCalculator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.AttractionRedisKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class AttractionCacheAspect {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Around(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.ExternalApiService.getOpenStreetMapAttractions(..)) && args(coordinateDTO, totalDays)")
    public Object cacheAttraction(ProceedingJoinPoint joinPoint, ICoordinateDTO coordinateDTO, int totalDays) {

        int limit = getLimit(totalDays);
        String redisKey = AttractionRedisKeyGenerator.generate(coordinateDTO);

        List<IAttractionDTO> cachedAttractions = fromCache(redisKey);

        if(cachedAttractions != null && limit <= cachedAttractions.size()) {
            log.debug("CACHE HIT: attractions found for coordinateDTO = {} is within the limit: {}", coordinateDTO,
                      limit);
            return cachedAttractions.stream().limit(limit).collect(Collectors.toList());
        }

        log.debug("CACHE MISS: attractions not found for coordinateDTO = {} or limit: {} exceeds number of cached " +
                          "attractions = {}", coordinateDTO, limit, cachedAttractions.size());
        List<IAttractionDTO> attractions;

        try {
            attractions  = new ArrayList<>(
                    Optional.ofNullable((List<AttractionDTO>) joinPoint.proceed())
                            .orElse(Collections.emptyList()));
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

    private List<IAttractionDTO> fromCache(String redisKey) {
        return new ArrayList<>(
                Optional.ofNullable((List<AttractionDTO>) redisTemplate.opsForValue().get(redisKey))
                        .orElse(Collections.emptyList()));
    }
}
