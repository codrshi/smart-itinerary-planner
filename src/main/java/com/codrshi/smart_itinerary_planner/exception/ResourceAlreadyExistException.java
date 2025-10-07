package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistException extends BaseException {
    public ResourceAlreadyExistException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.RESOURCE_ALREADY_EXIST.formatMessage(args), ErrorCode.RESOURCE_ALREADY_EXIST.getCode(), httpStatus);
    }
}
