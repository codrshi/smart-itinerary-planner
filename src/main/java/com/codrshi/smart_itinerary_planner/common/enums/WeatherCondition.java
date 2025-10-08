package com.codrshi.smart_itinerary_planner.common.enums;

public enum WeatherCondition {
    FAVOURABLE("Favourable weather"),
    FINE("Fine weather"),
    DISRUPTIVE("Disruptive weather"),
    UNFAVOURABLE("Unfavourable weather"),
    HAZARDOUS("Hazardous weather"),
    UNKNOWN("Unknown weather");
    private final String description;

    WeatherCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
