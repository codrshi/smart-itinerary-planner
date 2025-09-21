package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.GetItineraryRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;

@JsonDeserialize(as= GetItineraryRequestDTO.class)
public interface IGetItineraryRequestDTO  extends IFilterRequestDTO{
}
