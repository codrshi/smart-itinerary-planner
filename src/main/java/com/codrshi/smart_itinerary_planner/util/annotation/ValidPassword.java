package com.codrshi.smart_itinerary_planner.util.annotation;

import com.codrshi.smart_itinerary_planner.util.annotation.implementation.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid password. Password must be 8-20 characters long and include uppercase, lowercase, number and special character";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}