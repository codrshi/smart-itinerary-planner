package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IConstructActivitiesService {
    List<IActivityDTO> constructActivities(List<IEventDTO> events, List<IAttractionDTO> attractions,
                                                  Map<LocalDate, WeatherType> dateToWeatherMap);
}
