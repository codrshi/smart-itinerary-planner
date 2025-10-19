package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.dto.implementation.response.VirtualCrossingWeatherResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.mapper.IWeatherMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherMapper implements IWeatherMapper {

    public Map<LocalDate, WeatherType> mapDateToWeather(VirtualCrossingWeatherResponseDTO virtualCrossingWeatherResponseDTO) {

        if(virtualCrossingWeatherResponseDTO == null || virtualCrossingWeatherResponseDTO.getDays() == null) {
            return null;
        }

        return virtualCrossingWeatherResponseDTO.getDays().stream().collect(Collectors.toMap(
                days -> LocalDate.parse(days.getDatetime()),
                days -> WeatherType.fromLabel(days.getConditions())
        ));
    }
}
