package com.codrshi.smart_itinerary_planner.util;


import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils implements ConstraintValidator<ValidDateRange, ITimePeriodDTO> {

    private DateUtils() {
    }

    @Override
    public boolean isValid(ITimePeriodDTO timePeriod, ConstraintValidatorContext constraintValidatorContext) {
        if(timePeriod == null || timePeriod.getStartDate() == null || timePeriod.getEndDate() == null)
            return true;

        return !isDateRangeInvalid(timePeriod.getStartDate(), timePeriod.getEndDate());
    }

    @SneakyThrows
    public static int countDays(ITimePeriodDTO timePeriod){

        LocalDate startDate = timePeriod.getStartDate();
        LocalDate endDate = timePeriod.getEndDate();

        if(isDateRangeInvalid(startDate, endDate)){
            throw new InvalidDateRangeException(HttpStatus.BAD_REQUEST, startDate, endDate);
        }

        return Math.toIntExact(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    public static boolean isDateRangeInvalid(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isAfter(endDate);
    }

    public static boolean isDate(String field) {
        try {
            LocalDate.parse(field);
        }
        catch(Exception ex) {
            return false;
        }
        return true;
    }
}
