package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.generator.redis.ActivityRedisKeyGenerator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.ItineraryRedisKeyGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class GetItineraryCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Autowired
    private FactoryUtil factoryUtil;

    @Around(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.GetItineraryService.getItinerary(..)) && args(itineraryId)")
    public Object cacheGetItinerary(ProceedingJoinPoint joinPoint, String itineraryId) throws JsonProcessingException {

        String idRedisKey = ItineraryRedisKeyGenerator.generateWithItineraryId(itineraryId);
        String activitiesRedisKey = ActivityRedisKeyGenerator.generate(itineraryId);

        IItineraryResponseDTO responseDTO = fromCache(idRedisKey, activitiesRedisKey);

        if(responseDTO != null) {
            log.debug("CACHE HIT: cached itinerary found for itineraryId = {}", itineraryId);
            return responseDTO;
        }

        log.debug("CACHE MISS: cached itinerary not found for itineraryId = {}", itineraryId);
        try {
            responseDTO = (GetItineraryResponseDTO) joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        cacheResponse(idRedisKey, activitiesRedisKey, factoryUtil.copy(responseDTO, GetItineraryResponseDTO.class));

        return responseDTO;
    }

    private IItineraryResponseDTO fromCache(String idRedisKey, String activitiesRedisKey) {
        IItineraryResponseDTO responseDTO = (GetItineraryResponseDTO) redisTemplate.opsForValue().get(idRedisKey);
        List<IActivityDTO> activities = new ArrayList<>(
                        Optional.ofNullable((List<ActivityDTO>) redisTemplate.opsForValue().get(activitiesRedisKey))
                .orElse(Collections.emptyList()));

        if(responseDTO == null) {
            return null;
        }

        responseDTO.setActivities(activities);
        return responseDTO;
    }

    private void cacheResponse(String idRedisKey, String activitiesRedisKey, IItineraryResponseDTO responseDTO) {

        List<IActivityDTO> activities = responseDTO.getActivities();
        responseDTO.setActivities(null);

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForValue().set(idRedisKey, responseDTO, Duration.ofDays(itineraryProperties.getRedis().getItineraryTtl()));
                operations.opsForValue().set(activitiesRedisKey, activities, Duration.ofDays(itineraryProperties.getRedis().getItineraryTtl()));

                return operations.exec();
            }
        });
    }
}
