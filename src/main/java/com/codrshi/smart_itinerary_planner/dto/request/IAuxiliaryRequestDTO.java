package com.codrshi.smart_itinerary_planner.dto.request;

import com.codrshi.smart_itinerary_planner.dto.implementation.request.AuxiliaryRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as= AuxiliaryRequestDTO.class)
public interface IAuxiliaryRequestDTO {
    String getQuery();
    void setQuery(String query);
}
