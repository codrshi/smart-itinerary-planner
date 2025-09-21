package com.codrshi.smart_itinerary_planner.util.annotation;

import com.codrshi.smart_itinerary_planner.util.DateUtils;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DateUtils.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "timePeriod.startDate is after timePeriod.endDate";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
