package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Slf4j
public class AttractionRedisKeyGenerator extends RedisKeyGenerator {

    public static String generate(ICoordinateDTO coordinateDTO) {
        return generateKeyWithoutContext(Constant.ATTRACTION_KEY, String.format("{}_{}", coordinateDTO.getLatitude(), coordinateDTO.getLongitude()));
    }
}
