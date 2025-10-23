package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;

import java.time.LocalDate;

public class ItineraryRedisKeyGenerator extends RedisKeyGenerator {
    public static String generateWithItineraryId(String itineraryId) {
        return generateKeyWithContext(Constant.ITINERARY_KEY, itineraryId);
    }
}
