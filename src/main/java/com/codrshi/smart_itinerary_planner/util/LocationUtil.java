package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.LocationDTO;
import com.codrshi.smart_itinerary_planner.exception.InvalidCountryException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class LocationUtil {

    @Autowired
    private ItineraryProperties itineraryProperties;

    public ILocationDTO buildLocation(String city, String country) {

        String normalizedCity = normalizeLocation(city);
        CountryDetails countryDetails = toCountryCode(country);
        int radius = findRadius(countryDetails.getCountryCode());

        ILocationDTO locationDTO = new LocationDTO();
        locationDTO.setCity(normalizedCity);
        locationDTO.setCountry(countryDetails.getCountryName());
        locationDTO.setCountryCode(countryDetails.getCountryCode());
        locationDTO.setRadius(radius);

        return locationDTO;
    }

    private CountryDetails toCountryCode(String input) {

        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            CountryDetails countryDetails = new CountryDetails(locale.getDisplayCountry(Locale.ENGLISH),
                                                               locale.getCountry());

            if (countryDetails.getCountryCode().equalsIgnoreCase(input.trim()) ||
                    countryDetails.getCountryName().equalsIgnoreCase(normalizeLocation(input))) {
                return countryDetails;
            }
        }

        throw new InvalidCountryException(HttpStatus.BAD_REQUEST, input);
    }

    private String normalizeLocation(String location){
        return Arrays.stream(location.trim().toLowerCase().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0,1).toUpperCase()+word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private int findRadius(String countryCode) {
        int defaultRadius = itineraryProperties.getCityRadius().getDefaultRadius();
        return itineraryProperties.getCityRadius().getRadiusMapping().getOrDefault(countryCode, defaultRadius);
    }

    @AllArgsConstructor
    @Getter
    private class CountryDetails {
        private final String countryName;
        private final String countryCode;
    }
}
