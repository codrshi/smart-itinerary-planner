package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.OpenTripMapCoordinateResponseDTO;

public interface ICoordinateMapper {
    ICoordinateDTO mapToCoordinateDTO(OpenTripMapCoordinateResponseDTO openTripMapCoordinateResponseDTO);
}
