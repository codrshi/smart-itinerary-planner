package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import lombok.Data;

@Data
public class LocationDTO implements ILocationDTO {
    private String city;
    private String country;
    private String countryCode;
    private int radius;

    public String getDestination(){
        return String.format("%s, %s", city, country);
    }
}
