package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;

public class ItineraryRedisKeyGenerator extends RedisKeyGenerator {
    public static String generateWithItineraryId(String itineraryId) {
        return generateKeyWithContext(Constant.ITINERARY_KEY, itineraryId);
    }
}
