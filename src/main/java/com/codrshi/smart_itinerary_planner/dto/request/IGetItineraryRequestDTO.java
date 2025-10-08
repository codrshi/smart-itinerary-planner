package com.codrshi.smart_itinerary_planner.dto.request;

import com.codrshi.smart_itinerary_planner.dto.implementation.request.GetItineraryRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as= GetItineraryRequestDTO.class)
public interface IGetItineraryRequestDTO extends IFilterRequestDTO {
}
