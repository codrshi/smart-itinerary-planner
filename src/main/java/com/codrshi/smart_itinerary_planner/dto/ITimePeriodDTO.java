package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;

@JsonDeserialize(as= TimePeriodDTO.class)
public interface ITimePeriodDTO {
    LocalDate getStartDate();
    void setStartDate(LocalDate startDate);

    LocalDate getEndDate();
    void setEndDate(LocalDate endDate);

    ITimePeriodDTO clone();
}
