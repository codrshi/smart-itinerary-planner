package com.codrshi.smart_itinerary_planner.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
    EVENT("event"),
    ATTRACTION("attraction");

    private final String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
