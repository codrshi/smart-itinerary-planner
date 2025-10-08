package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.implementation.response.VirtualCrossingWeatherResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;
import java.util.Map;

public interface IWeatherMapper {
    Map<LocalDate, WeatherType> mapDateToWeather(VirtualCrossingWeatherResponseDTO virtualCrossingWeatherResponseDTO);
}
