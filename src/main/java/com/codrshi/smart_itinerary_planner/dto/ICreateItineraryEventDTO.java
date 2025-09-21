package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ICreateItineraryEventDTO {
    public IUserRefDTO getUserRef();
    public void setUserRef(IUserRefDTO userRef);

    public List<IAttractionDTO> getAttractions();
    public void setAttractions(List<IAttractionDTO> attractions);

    public List<IEventDTO> getEvents();
    public void setEvents(List<IEventDTO> events);

    public Map<LocalDate, WeatherType> getDateToWeatherMap();
    public void setDateToWeatherMap(Map<LocalDate, WeatherType> dateToWeatherMap);

    public ITimePeriodDTO getTimePeriod();
    public void setTimePeriod(ITimePeriodDTO timePeriod);

    public ILocationDTO getLocation();
    public void setLocation(ILocationDTO location);

    public String getItineraryId();
    public void setItineraryId(String itineraryId);


}
