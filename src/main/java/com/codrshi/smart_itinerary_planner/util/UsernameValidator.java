package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.util.annotation.ValidLocation;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String USERNAME_REGEX = "^(?=.{3,20}$)(?!.*[_.]{2})[a-zA-Z][a-zA-Z0-9._]*[a-zA-Z0-9]$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if(username == null ||username.isEmpty()) {
            return true;
        }

        return username.matches(USERNAME_REGEX);
    }
}
