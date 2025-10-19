package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(as = AttractionDTO.class)
public interface IAttractionDTO extends IPointOfInterestDTO {
}
