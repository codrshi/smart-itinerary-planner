package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BaseException {

    public TooManyRequestException() {
        super(ErrorCode.TOO_MANY_REQUEST.getMessageTemplate(),
              ErrorCode.TOO_MANY_REQUEST.getCode(), HttpStatus.TOO_MANY_REQUESTS);
    }
}
