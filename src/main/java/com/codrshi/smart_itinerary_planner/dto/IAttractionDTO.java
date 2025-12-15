package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = AttractionDTO.class)
public interface IAttractionDTO extends IPointOfInterestDTO {
    String getAddress();
    void setAddress(String address);
}
