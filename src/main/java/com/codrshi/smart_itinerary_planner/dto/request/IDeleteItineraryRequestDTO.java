package com.codrshi.smart_itinerary_planner.dto.request;

import com.codrshi.smart_itinerary_planner.dto.implementation.request.DeleteItineraryRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as= DeleteItineraryRequestDTO.class)
public interface IDeleteItineraryRequestDTO extends IFilterRequestDTO {

}
