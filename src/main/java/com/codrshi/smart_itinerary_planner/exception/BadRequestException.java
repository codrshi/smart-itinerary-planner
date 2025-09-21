package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class BadRequestException extends BaseException {

    public BadRequestException(ErrorCode errorCode, String ...args) {
        super(errorCode.formatMessage(args), errorCode.getCode(), HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, null, HttpStatus.BAD_REQUEST);
    }
}
