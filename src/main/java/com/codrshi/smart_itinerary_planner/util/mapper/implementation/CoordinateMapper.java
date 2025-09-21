package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.OpenTripMapCoordinateResponseDTO;
import com.codrshi.smart_itinerary_planner.util.mapper.ICoordinateMapper;

public class CoordinateMapper implements ICoordinateMapper {


    @Override
    public ICoordinateDTO mapToCoordinateDTO(OpenTripMapCoordinateResponseDTO openTripMapCoordinateResponseDTO) {
        ICoordinateDTO coordinateDTO = new CoordinateDTO();

        coordinateDTO.setLatitude(openTripMapCoordinateResponseDTO.getLat());
        coordinateDTO.setLongitude(openTripMapCoordinateResponseDTO.getLon());

        return coordinateDTO;
    }
}
