package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.RESOURCE_NOT_FOUND.formatMessage(args), ErrorCode.RESOURCE_NOT_FOUND.getCode(), httpStatus);
    }
}
