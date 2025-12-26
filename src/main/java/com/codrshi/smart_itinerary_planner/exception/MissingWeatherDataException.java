package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class MissingWeatherDataException extends BaseException {
    public MissingWeatherDataException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.MISSING_WEATHER_DATA.formatMessage(args), ErrorCode.MISSING_WEATHER_DATA.getCode(), httpStatus);
    }
}
