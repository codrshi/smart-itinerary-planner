package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ICreateItineraryEventDTO {
    String getUserRef();
    void setUserRef(String userRef);

    List<IAttractionDTO> getAttractions();
    void setAttractions(List<IAttractionDTO> attractions);

    List<IEventDTO> getEvents();
    void setEvents(List<IEventDTO> events);

    Map<LocalDate, WeatherType> getDateToWeatherMap();
    void setDateToWeatherMap(Map<LocalDate, WeatherType> dateToWeatherMap);

    ITimePeriodDTO getTimePeriod();
    void setTimePeriod(ITimePeriodDTO timePeriod);

    ILocationDTO getLocation();
    void setLocation(ILocationDTO location);

    String getItineraryId();
    void setItineraryId(String itineraryId);


}
