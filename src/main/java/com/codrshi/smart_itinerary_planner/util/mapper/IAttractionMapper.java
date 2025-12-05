package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GeoapifyAttractionResponseDTO;

import java.util.List;

public interface IAttractionMapper {
    List<IAttractionDTO> mapToAttractionDTO(GeoapifyAttractionResponseDTO geoapifyAttractionResponseDTO);
}
