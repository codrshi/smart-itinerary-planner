package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;

import java.time.LocalDate;

public interface IWeatherDTO {
    LocalDate getDate();

    void setDate(LocalDate date);

    WeatherType getWeatherType();

    void setWeatherType(WeatherType weatherType);
}
