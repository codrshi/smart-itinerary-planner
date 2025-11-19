package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherRedisKeyGenerator extends RedisKeyGenerator{

    public static String generate(ICoordinateDTO coordinateDTO) {
        return generateKeyWithoutContext(Constant.WEATHER_KEY, String.format("%s_%s", coordinateDTO.getLatitude(), coordinateDTO.getLongitude()));

    }
}
