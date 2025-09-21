package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.LocationDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = LocationDTO.class)
public interface ILocationDTO {
    String getCity();

    void setCity(String city);

    String getCountry();

    void setCountry(String country);

    String getCountryCode();

    void setCountryCode(String countryCode);

    int getRadius();

    void setRadius(int radius);

    String getDestination();
}
