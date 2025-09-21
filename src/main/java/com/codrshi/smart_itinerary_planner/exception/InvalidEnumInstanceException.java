package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;


public class InvalidEnumInstanceException extends BaseException {

    public InvalidEnumInstanceException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.INVALID_ENUM_INSTANCE.formatMessage(args), ErrorCode.INVALID_ENUM_INSTANCE.getCode(), httpStatus);
    }
}
