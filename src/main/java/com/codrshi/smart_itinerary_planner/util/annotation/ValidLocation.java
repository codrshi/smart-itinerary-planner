package com.codrshi.smart_itinerary_planner.util.annotation;

import com.codrshi.smart_itinerary_planner.util.annotation.implementation.LocationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = LocationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocation {
    String message() default "Invalid location";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
