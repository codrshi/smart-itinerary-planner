package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.util.annotation.ValidLocation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LocationValidator implements ConstraintValidator<ValidLocation, String> {

    private static final String LOCATION_REGEX = "^[\\p{L} .'-]+$";

    @Override
    public boolean isValid(String location, ConstraintValidatorContext constraintValidatorContext) {
        if(location == null ||location.isEmpty()) {
            return true;
        }

        return location.matches(LOCATION_REGEX);
    }
}
