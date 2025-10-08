package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.GetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.exception.InvalidItineraryIdFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationServiceTest extends BaseTest {

    @DisplayName("Should throw IllegalArgumentException when itineraryId is null or blank")
    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void whenItixIdIsNullOrBlank_thenThrowError(String invalidId) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateItineraryId(invalidId, HttpStatus.BAD_REQUEST)
        );
        assertEquals(Constant.ERR_MSG_MISSING_ITINERARY_ID, ex.getMessage());
    }

    @DisplayName("Should throw InvalidItineraryIdFormatException for IDs not matching regex")
    @ParameterizedTest
    @ValueSource(strings = {
            "ITIX-",                  // missing region + details
            "ITIX-ABC",               // missing details
            "ITIX-A-12345",           // region too short
            "ITIX-ABCD-12345",        // region too long
            "XYZ-AB-123",             // wrong prefix
            "ITIX-12-XYZ"             // region not alphabets
    })
    void whenInvalidItixId_thenThrowError(String invalidId) {
        InvalidItineraryIdFormatException ex = assertThrows(
                InvalidItineraryIdFormatException.class,
                () -> validationService.validateItineraryId(invalidId, HttpStatus.BAD_REQUEST)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(ErrorCode.INVALID_ITINERARY_ID_FORMAT.getCode(), ex.getErrorCode());
    }

    @DisplayName("Should pass for valid itinerary IDs")
    @ParameterizedTest
    @ValueSource(strings = {
            "ITIX-IN-12345",
            "ITIX-USA-XYZ",
            "ITIX-UK-ABCDEF",
            "ITIX-FR-999"
    })
    void whenValidItixId_thenPass(String validId) {
        assertDoesNotThrow(() -> validationService.validateItineraryId(validId, null));
    }

    @DisplayName("Should pass for valid GetItineraryRequestDTO")
    @ParameterizedTest
    @EnumSource(DateRangeCriteria.class)
    @NullSource
    void givenValidGetItineraryRequest_whenValidate_thenPasses(DateRangeCriteria dateRangeCriteria) {
        IGetItineraryRequestDTO dto = new GetItineraryRequestDTO();
        dto.setStartDate(LocalDate.of(2025, 1, 1));
        dto.setEndDate(LocalDate.of(2025, 1, 10));
        dto.setDateRangeCriteria(dateRangeCriteria);

        assertDoesNotThrow(() -> validationService.validateFilterItineraryRequest(dto));
    }

    @DisplayName("Should throw InvalidDateRangeException for invalid date range")
    @Test
    void givenStartDateAfterEndDate_whenValidate_thenThrowInvalidDateRange() {
        IGetItineraryRequestDTO dto = new GetItineraryRequestDTO();
        dto.setStartDate(LocalDate.of(2025, 1, 10));
        dto.setEndDate(LocalDate.of(2025, 1, 1));

        InvalidDateRangeException ex = assertThrows(
                InvalidDateRangeException.class,
                () -> validationService.validateFilterItineraryRequest(dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(ErrorCode.INVALID_DATE_RANGE.getCode(), ex.getErrorCode());
    }

    @DisplayName("Should throw BadRequestException for missing dates when criteria is provided")
    @ParameterizedTest
    @MethodSource("criteriaAndMissingDateProvider")
    void givenCriteriaButMissingDates_whenValidate_thenThrowError(
            LocalDate startDate, LocalDate endDate
    ) {
        IGetItineraryRequestDTO dto = new GetItineraryRequestDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setDateRangeCriteria(DateRangeCriteria.CONTAINS_DATE_RANGE);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> validationService.validateFilterItineraryRequest(dto)
        );
        assertEquals(ErrorCode.MISSING_DATE_WHEN_CRITERIA_PROVIDED.getCode(), ex.getErrorCode());
    }

    static Stream<Arguments> criteriaAndMissingDateProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null, LocalDate.of(2025, 1, 10)),
                org.junit.jupiter.params.provider.Arguments.of(LocalDate.of(2025, 1, 1), null),
                org.junit.jupiter.params.provider.Arguments.of(null, null)
        );
    }

}
