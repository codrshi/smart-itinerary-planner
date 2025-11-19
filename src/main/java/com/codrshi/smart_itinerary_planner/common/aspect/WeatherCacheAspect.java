package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class WeatherCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Around(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.ExternalApiService.getVirtualCrossingWeather(..)) && args(timePeriodDTO, coordinateDTO)")
    public Object cacheWeather(ProceedingJoinPoint joinPoint, ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {

        String redisKey = WeatherRedisKeyGenerator.generate(coordinateDTO);

        Map<LocalDate, WeatherType> cachedDateToWeatherMap = fromCache(redisKey);
        ITimePeriodDTO missingPeriodDTO = getMissingPeriod(timePeriodDTO, cachedDateToWeatherMap);

        Map<LocalDate, WeatherType> partialDateToWeatherMap = extractDateToWeatherMapFromCache(cachedDateToWeatherMap, timePeriodDTO);

        if(missingPeriodDTO == null) {
            log.debug("CACHE HIT: Weather details for full time period = {} exists in cache.", timePeriodDTO);
            return partialDateToWeatherMap;
        }

        log.debug("CACHE MISS: Weather details missing for time period = {}", missingPeriodDTO);
        Map<LocalDate, WeatherType> remainingDateToWeatherMap;

        try {
            remainingDateToWeatherMap  =
                    (Map<LocalDate, WeatherType>) joinPoint.proceed(new Object[] {missingPeriodDTO, coordinateDTO});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        redisTemplate.opsForHash().putAll(redisKey, remainingDateToWeatherMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
        redisTemplate.expire(redisKey, Duration.ofDays(itineraryProperties.getRedis().getWeatherTtl()));

        return Stream.concat(partialDateToWeatherMap.entrySet().stream(),
                             remainingDateToWeatherMap.entrySet().stream());
    }

    private Map<LocalDate, WeatherType> fromCache(String redisKey) {
        return redisTemplate.opsForHash().entries(redisKey).entrySet().stream()
                .collect(Collectors.toMap(e -> LocalDate.parse(e.getKey().toString()), e -> (WeatherType) e.getValue()));
    }

    private ITimePeriodDTO getMissingPeriod(ITimePeriodDTO timePeriodDTO, Map<LocalDate, WeatherType> cachedDateToWeatherMap) {

        if(cachedDateToWeatherMap == null) {
            return timePeriodDTO; // cache miss; return the original time period
        }

        LocalDate startDate = timePeriodDTO.getStartDate();
        LocalDate endDate = timePeriodDTO.getEndDate().plusDays(1);
        LocalDate missingStartDate = null;
        LocalDate missingEndDate = null;

        for(LocalDate currentDate = startDate; currentDate.isBefore(endDate); currentDate=currentDate.plusDays(1)) {
            if(!cachedDateToWeatherMap.containsKey(currentDate)) {
                if(missingEndDate == null) {
                    missingStartDate = currentDate;
                    missingEndDate = currentDate;
                } else if(missingEndDate.isEqual(currentDate.minusDays(1))) {
                    missingEndDate = currentDate;
                } else {
                    return timePeriodDTO; // cache miss; return the original time period
                }
            }
        }

        if(missingStartDate == null) {
            return null;   // cache hit; return null
        }

        return new TimePeriodDTO(missingStartDate, missingEndDate);
    }

    private Map<LocalDate, WeatherType> extractDateToWeatherMapFromCache(Map<LocalDate, WeatherType> cachedDateToWeatherMap, ITimePeriodDTO timePeriodDTO) {
        return cachedDateToWeatherMap.entrySet().stream()
                .filter(e -> e.getKey().isAfter(timePeriodDTO.getStartDate().minusDays(1)) && e.getKey().isBefore(timePeriodDTO.getEndDate().plusDays(1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
