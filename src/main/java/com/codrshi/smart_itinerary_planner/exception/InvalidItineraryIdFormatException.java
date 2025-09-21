package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InvalidItineraryIdFormatException extends BaseException {
    public InvalidItineraryIdFormatException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.INVALID_ITINERARY_ID_FORMAT.formatMessage(args), ErrorCode.INVALID_ITINERARY_ID_FORMAT.getCode(), httpStatus);
    }
}
