package com.codrshi.smart_itinerary_planner.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.UUID;

import static com.codrshi.smart_itinerary_planner.common.Constant.ITINERARY_ID_PREFIX;

public class ItineraryIdGenerator {
    private ItineraryIdGenerator() {}

    public static String generateItineraryId(String countryCode) {
        return String.format("%s%s-%s", ITINERARY_ID_PREFIX, countryCode, NanoIdUtils.randomNanoId().toUpperCase());
    }
}
