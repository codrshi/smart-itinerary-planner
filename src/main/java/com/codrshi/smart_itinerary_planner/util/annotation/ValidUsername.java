package com.codrshi.smart_itinerary_planner.util.annotation;

import com.codrshi.smart_itinerary_planner.util.annotation.implementation.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "Invalid username. Username should be between 3-20 characters long, start with a letter and can contain only letters, numbers, underscores or dots.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
