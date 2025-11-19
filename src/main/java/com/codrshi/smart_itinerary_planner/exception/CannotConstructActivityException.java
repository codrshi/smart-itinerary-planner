package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class CannotConstructActivityException extends BaseException {

    public CannotConstructActivityException() {
        super(ErrorCode.CANNOT_CONSTRUCT_ACTIVITIES.getMessageTemplate(),
              ErrorCode.CANNOT_CONSTRUCT_ACTIVITIES.getCode(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}