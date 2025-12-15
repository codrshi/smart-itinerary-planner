package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidCountryException extends BaseException {

    public InvalidCountryException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.INVALID_COUNTRY_NAME.formatMessage(args), ErrorCode.INVALID_COUNTRY_NAME.getCode(), httpStatus);
    }
}
