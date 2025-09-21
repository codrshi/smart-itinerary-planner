package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class DateUtilsTest {

    @ParameterizedTest
    @CsvSource(value = {
            "2025-06-01, 2025-06-10, 10",
            "2025-06-29, 2025-06-29, 1",
            "2025-06-01, 2025-08-05, 66"
    })
    void givenDateUtilsCountDays_whenDifferentInputs_thenCorrectOutputs(LocalDate startDate, LocalDate endDate,
                                                                        int expectedTotalDays) {

        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(startDate);
        timePeriodDTO.setEndDate(endDate);
        Assertions.assertEquals(expectedTotalDays, DateUtils.countDays(timePeriodDTO));

    }

    @Test
    void givenDateUtilsCountDays_whenEndDateBeforeStartDate_thenThrowException(){
        LocalDate startDate = LocalDate.of(2025, 6, 10);
        LocalDate endDate = LocalDate.of(2025, 6, 1);
        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(startDate);
        timePeriodDTO.setEndDate(endDate);

        Assertions.assertThrows(InvalidDateRangeException.class, () -> DateUtils.countDays(timePeriodDTO));
    }
}
