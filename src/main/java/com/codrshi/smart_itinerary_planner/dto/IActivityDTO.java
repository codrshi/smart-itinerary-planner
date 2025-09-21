package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;
import java.util.List;

@JsonDeserialize(as = ActivityDTO.class)
public interface IActivityDTO {
    String getActivityId();
    void setActivityId(String activityId);

    LocalDate getActivityDate();
    void setActivityDate(LocalDate activityDate);

    WeatherType getWeatherType();
    void setWeatherType(WeatherType weather);

    String getActivityNote();
    void setActivityNote(String activityNote);

    List<IPointOfInterestDTO> getPointOfInterests();
    void setPointOfInterests(List<IPointOfInterestDTO> pointOfInterests);
}
