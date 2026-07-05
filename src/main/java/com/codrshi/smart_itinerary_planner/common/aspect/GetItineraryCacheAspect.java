package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
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
import java.util.List;
import java.util.Map;

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

        String idRedisKey = ItineraryRedisKeyGenerator.generate(itineraryId);

        IItineraryResponseDTO responseDTO = fromCache(idRedisKey);

        if(responseDTO != null) {
            log.debug("CACHE HIT: cached itinerary found for itineraryId = {}", itineraryId);
            return responseDTO;
        }

        log.debug("CACHE MISS: cached itinerary not found for itineraryId = {}", itineraryId);
        try {
            responseDTO = (GetItineraryResponseDTO) joinPoint.proceed();
        } catch(ResourceNotFoundException e){
            throw e;
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }

        cacheResponse(idRedisKey, factoryUtil.copy(responseDTO, GetItineraryResponseDTO.class));

        return responseDTO;
    }

    private IItineraryResponseDTO fromCache(String idRedisKey) {
        List<Object> values = redisTemplate.opsForHash().multiGet(
                idRedisKey,
                List.of(Constant.ITINERARY_KEY_METADATA, Constant.ITINERARY_KEY_ACTIVITIES));

        Object metadataRaw = values.get(0);
        Object activitiesRaw = values.get(1);

        if(metadataRaw == null || activitiesRaw == null) {
            return null;
        }

        GetItineraryResponseDTO responseDTO = (GetItineraryResponseDTO) metadataRaw;
        List<ActivityDTO> activities = (List<ActivityDTO>) activitiesRaw;
        responseDTO.setActivities(new ArrayList<>(activities));
        return responseDTO;
    }

    private void cacheResponse(String idRedisKey, IItineraryResponseDTO responseDTO) {

        List<IActivityDTO> activities = responseDTO.getActivities();
        responseDTO.setActivities(null);

        Map<String, Object> map = Map.of(Constant.ITINERARY_KEY_METADATA, responseDTO,
                                         Constant.ITINERARY_KEY_ACTIVITIES, activities);

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForHash().putAll(idRedisKey, map);
                operations.expire(idRedisKey, Duration.ofDays(itineraryProperties.getRedis().getItineraryTtl()));

                return operations.exec();
            }
        });
    }
}
