package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GeoapifyAttractionResponseDTO;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class AttractionMapper implements IAttractionMapper {

    @Autowired
    public CounterManager counterManager;

    @Override
    public List<IAttractionDTO> mapToAttractionDTO(GeoapifyAttractionResponseDTO geoapifyAttractionResponseDTO) {

        if (geoapifyAttractionResponseDTO == null || geoapifyAttractionResponseDTO.getFeatures() == null) {
            return Collections.emptyList();
        }

        return geoapifyAttractionResponseDTO.getFeatures().stream()
                .filter(feature -> feature != null && feature.getProperties() != null)
                .map(feature -> mapToAttractionDTO(feature.getProperties()))
                .toList();
    }

    private IAttractionDTO mapToAttractionDTO(GeoapifyAttractionResponseDTO.Properties properties) {
        IAttractionDTO attractionDTO = new AttractionDTO();

        attractionDTO.setPoiId(counterManager.nextPoiId());
        attractionDTO.setName(properties.getName());
        attractionDTO.setCategory(properties.getCategories());
        attractionDTO.setAddress(properties.getFormatted());
        //attractionDTO.setActivityType(ActivityType.ATTRACTION);
        attractionDTO.setNote(Constant.EMPTY_NOTE);

        return attractionDTO;
    }
}
