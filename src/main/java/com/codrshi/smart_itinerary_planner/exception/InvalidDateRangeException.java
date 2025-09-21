package com.codrshi.smart_itinerary_planner.exception;


import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends BaseException {

    public InvalidDateRangeException(HttpStatus httpStatus, Object... args) {
        super(ErrorCode.INVALID_DATE_RANGE.formatMessage(args), ErrorCode.INVALID_DATE_RANGE.getCode(), httpStatus);
    }
}
