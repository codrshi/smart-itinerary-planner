package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ApiResponseWrapper;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.util.generator.redis.ActivityRedisKeyGenerator;
import com.codrshi.smart_itinerary_planner.util.generator.redis.AuxiliaryRedisKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Aspect
@Order(2)
@Component
@Slf4j
public class PatchItineraryCacheAspect {

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.PatchItineraryService.patchItinerary(..))",
                    returning = "responseDTO")
    public void cachePatchItineraryResponse(JoinPoint joinPoint, IItineraryResponseDTO responseDTO) {

        String itineraryId = responseDTO.getItineraryId();
        List<IActivityDTO> activities = responseDTO.getActivities();

        String activitiesRedisKey = ActivityRedisKeyGenerator.generate(itineraryId);
        String mailedItineraryKey = AuxiliaryRedisKeyGenerator.generateMailedItinerary(itineraryId);

        log.debug("CACHE UPDATE: updating keys with new activities for itineraryId = {} in cache", itineraryId);

        redisTemplate.opsForValue().set(activitiesRedisKey, activities, Duration.ofDays(itineraryProperties.getRedis().getItineraryTtl()));
        redisTemplate.opsForSet().remove(mailedItineraryKey, itineraryId);
    }
}
