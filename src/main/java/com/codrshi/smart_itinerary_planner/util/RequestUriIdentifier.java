package com.codrshi.smart_itinerary_planner.util;

import java.util.Arrays;

public class RequestUriIdentifier {
    public static boolean match(String uri, String[] endpoints){
        return Arrays.stream(endpoints).anyMatch(uri::contains);
    }
}
