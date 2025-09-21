package com.codrshi.smart_itinerary_planner.common.enums;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DateRangeCriteria {
    SAME_DATE_RANGE("sameDateRange"),
    WITHIN_DATE_RANGE("withinDateRange"),
    CONTAINS_DATE_RANGE("containsDateRange"),
    INTERSECTS_DATE_RANGE("intersectsDateRange");

    private final String value;

    DateRangeCriteria(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DateRangeCriteria fromString(String value) {
        for (DateRangeCriteria type : DateRangeCriteria.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidEnumInstanceException(HttpStatus.BAD_REQUEST, Constant.DATE_RANGE_CRITERIA, value, getValues());
    }

    private static List<String> getValues() {
        return Arrays.stream(DateRangeCriteria.values()).map(DateRangeCriteria::getValue).collect(Collectors.toList());
    }
}
