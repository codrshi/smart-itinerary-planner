package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IWeatherDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WeatherDTO implements IWeatherDTO {
    private LocalDate date;
    private WeatherType weatherType;
}
