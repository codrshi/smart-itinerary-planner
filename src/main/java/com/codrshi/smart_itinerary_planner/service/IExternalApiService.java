package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IExternalApiService {
    List<IEventDTO> getTicketmasterEvents(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO);

    List<IAttractionDTO> getOpenStreetMapAttractions(int radius, ICoordinateDTO coordinateDTO, int totalDays);

    ICoordinateDTO getOpenStreetMapCoordinate(ILocationDTO locationDTO);

    Map<LocalDate, WeatherType> getVirtualCrossingWeather(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO);
}
