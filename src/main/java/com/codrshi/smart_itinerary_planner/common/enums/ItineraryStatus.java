package com.codrshi.smart_itinerary_planner.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItineraryStatus {
    STARTED("started"),
    SUCCESS("success"),
    FAILED("failed");

    private final String value;

    ItineraryStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
