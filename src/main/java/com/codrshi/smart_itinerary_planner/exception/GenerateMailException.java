package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class GenerateMailException extends BaseException{

    public GenerateMailException() {
        super(ErrorCode.TEMPORARY_BLACKLISTED.getMessageTemplate(), ErrorCode.TEMPORARY_BLACKLISTED.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
