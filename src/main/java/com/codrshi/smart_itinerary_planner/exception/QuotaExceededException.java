package com.codrshi.smart_itinerary_planner.exception;

import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;

public class QuotaExceededException extends BaseException {

        public QuotaExceededException(String ...args) {
            super(ErrorCode.QUOTA_EXCEEDED.formatMessage(args), ErrorCode.QUOTA_EXCEEDED.getCode(), null);
        }
}
