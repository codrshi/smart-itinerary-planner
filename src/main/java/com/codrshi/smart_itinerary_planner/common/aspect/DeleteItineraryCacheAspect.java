package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ApiResponseWrapper;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.util.generator.redis.ActivityRedisKeyGenerator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.AuxiliaryRedisKeyGenerator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.ItineraryRedisKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Aspect
@Order(2)
@Component
@Slf4j
public class DeleteItineraryCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.DeleteItineraryService.deleteItinerary(..)) && args(itineraryId)")
    public void cacheDeleteItineraryResponse(JoinPoint joinPoint, String itineraryId) {
        deleteFromCache(itineraryId);
    }

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.DeleteItineraryService.deleteItineraries(..))",
                    returning = "responseDTO")
    public void cacheDeleteItinerariesResponse(JoinPoint joinPoint, IDeleteItineraryResponseDTO responseDTO) {
        responseDTO.getItineraryIds().forEach(itineraryId -> deleteFromCache(itineraryId));
    }

    private void deleteFromCache(String itineraryId) {
        String idRedisKey = ItineraryRedisKeyGenerator.generateWithItineraryId(itineraryId);
        String activitiesRedisKey = ActivityRedisKeyGenerator.generate(itineraryId);
        String mailedItineraryKey = AuxiliaryRedisKeyGenerator.generateMailedItinerary(itineraryId);

        log.debug("CACHE CLEAR: deleting keys for itineraryId = {} from cache", itineraryId);
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.delete(idRedisKey);
                operations.delete(activitiesRedisKey);
                operations.opsForSet().remove(mailedItineraryKey, itineraryId);

                return operations.exec();
            }
        });
    }
}