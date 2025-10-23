package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;

public class ActivityRedisKeyGenerator extends RedisKeyGenerator{
    public static String generate(String itineraryId) {
        return generateKeyWithContext(Constant.ACTIVITY_KEY, itineraryId);
    }
}
