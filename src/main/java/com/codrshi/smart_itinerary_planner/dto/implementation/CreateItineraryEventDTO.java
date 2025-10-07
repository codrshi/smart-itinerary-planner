package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRefDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CreateItineraryEventDTO implements ICreateItineraryEventDTO {
    private String userRef;
    private List<IAttractionDTO> attractions;
    private List<IEventDTO> events;
    private Map<LocalDate, WeatherType> dateToWeatherMap;
    private ITimePeriodDTO timePeriod;
    private ILocationDTO location;
    private String itineraryId;
}
