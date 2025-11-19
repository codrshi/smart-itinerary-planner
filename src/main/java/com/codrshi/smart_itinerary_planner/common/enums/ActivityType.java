package com.codrshi.smart_itinerary_planner.common.enums;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonCreator
    public static ActivityType fromString(String value) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidEnumInstanceException(HttpStatus.BAD_REQUEST, Constant.ACTIVITY_TYPE, value, getValues());
    }

    private static List<String> getValues() {
        return Arrays.stream(ActivityType.values()).map(ActivityType::getValue).collect(Collectors.toList());
    }
}
