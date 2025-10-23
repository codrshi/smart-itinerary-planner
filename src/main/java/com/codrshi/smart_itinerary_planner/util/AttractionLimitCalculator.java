package com.codrshi.smart_itinerary_planner.util;


public class AttractionLimitCalculator {
    public static int calculate(int totalDays, double base, double scale, int maxLimit) {
        double limit = base + scale * Math.log(totalDays + 1);
        int roundedLimit = (int) Math.round(limit);

        return Math.max((int) base, Math.min(roundedLimit, maxLimit));
    }
}
