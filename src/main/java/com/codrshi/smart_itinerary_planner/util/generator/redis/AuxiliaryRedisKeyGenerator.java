package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;

public class AuxiliaryRedisKeyGenerator extends RedisKeyGenerator{

    public static String generateMailedItinerary(String itineraryId) {
        return generateKeyWithContext(Constant.MAILED_ITINERARIES_KEY, itineraryId);
    }

    public static String generateMailedItinerary(String username, String itineraryId) {
        return generateKeyMissingContext(username, Constant.MAILED_ITINERARIES_KEY, itineraryId);
    }

    public static String generateBlacklistedMail() {
        return generateKeyWithoutContext(Constant.BLACKLISTED_MAILS_KEY);
    }
}
