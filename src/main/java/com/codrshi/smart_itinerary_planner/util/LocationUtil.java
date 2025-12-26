package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.LocationDTO;
import com.codrshi.smart_itinerary_planner.exception.InvalidCountryException;
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
        CountryDetails countryDetails = getCountryDetails(country);
        int radius = findRadius(countryDetails.countryCode());

        ILocationDTO locationDTO = new LocationDTO();
        locationDTO.setCity(normalizedCity);
        locationDTO.setCountry(countryDetails.countryName());
        locationDTO.setCountryCode(countryDetails.countryCode());
        locationDTO.setRadius(radius);

        return locationDTO;
    }

    public String toCountryCode(String input) {
        return getCountryDetails(input).countryCode();
    }

    private CountryDetails getCountryDetails(String input) {

        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            CountryDetails countryDetails = new CountryDetails(locale.getDisplayCountry(Locale.ENGLISH),
                                                               locale.getCountry());

            if (countryDetails.countryCode().equalsIgnoreCase(input.trim()) ||
                    countryDetails.countryName().equalsIgnoreCase(normalizeLocation(input))) {
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

    private record CountryDetails(String countryName, String countryCode) {
    }
}
