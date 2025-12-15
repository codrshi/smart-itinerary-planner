package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UtilsTest extends BaseTest {

    @Autowired
    private LocationUtil locationUtil;

    @ParameterizedTest
    @CsvSource(value = {
            "Paris| France| Paris, France",
            "paris| FRANCE| Paris, France",
            "new DElhi|  india  | New Delhi, India"
    }, delimiter = '|')
    void givenLocationUtilBuildDestination_whenDifferentInputs_thenCorrectOutputs(String city, String country,
                                                                             String expectedDestination) {
        ILocationDTO locationDTO = locationUtil.buildLocation(city, country);

        Assertions.assertEquals(expectedDestination, locationDTO.getDestination());

    }

    @ParameterizedTest
    @CsvSource(value = {
            "France | FR | France",
            "Burkina Faso | BF | Burkina Faso",
            "MY | MY | Malaysia",
            "Singapore | SG | Singapore",
            "PG | PG | Papua New Guinea",
            "United Arab Emirates | AE | United Arab Emirates"
    }, delimiter = '|')
    void givenLocationUtilToCountryCode_whenDifferentInputs_thenCorrectOutputs(String country,
                                                                             String expectedCountryCode, String expectedCountryName) {
        ILocationDTO locationDTO = locationUtil.buildLocation("", country);
        Assertions.assertEquals(expectedCountryCode, locationDTO.getCountryCode());

    }
}
