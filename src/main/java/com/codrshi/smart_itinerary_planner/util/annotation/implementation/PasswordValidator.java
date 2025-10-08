package com.codrshi.smart_itinerary_planner.util.annotation.implementation;

import com.codrshi.smart_itinerary_planner.util.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if(password == null ||password.isEmpty()) {
            return true;
        }

        return password.matches(PASSWORD_REGEX);
    }
}