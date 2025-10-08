package com.codrshi.smart_itinerary_planner.dto.request;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.CreateItineraryRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as= CreateItineraryRequestDTO.class)
public interface ICreateItineraryRequestDTO {
    String getCity();
    void setCity(String city);

    String getCountry();
    void setCountry(String country);

    ITimePeriodDTO getTimePeriod();
    void setTimePeriod(ITimePeriodDTO timePeriod);
}
