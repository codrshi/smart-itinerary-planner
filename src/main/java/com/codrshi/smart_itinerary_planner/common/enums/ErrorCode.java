package com.codrshi.smart_itinerary_planner.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(1000, "An unexpected error occurred."),
    INVALID_DATE_RANGE(10004, "The date range from %s to %s is invalid."),
    INVALID_COUNTRY_NAME(10005, "Invalid country name or code. Cannot find locale for %s."),
    RESOURCE_NOT_FOUND(10006, "No %s found for the given request."),
    MISSING_WEATHER_DATA(10007, "Weather details were fetched for %s day(s) instead of %s day(s)."),
    INVALID_ITINERARY_ID_FORMAT(10008, "Invalid itinerary ID format. Expected format: %s, actual value: %s"),
    INVALID_ENUM_INSTANCE(10009, "Invalid %s value: %s, valid values are: %s"),
    MISSING_DATE_WHEN_CRITERIA_PROVIDED(10010, "Expected both 'startDate' and 'endDate' when dateRangeCriteria is provided.");

    private final int code;
    private final String messageTemplate;

    ErrorCode(int code, String messageTemplate){
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String formatMessage(Object... args) {
        return String.format(this.messageTemplate, args);
    }


}
