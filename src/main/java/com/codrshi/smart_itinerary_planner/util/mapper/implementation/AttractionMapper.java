package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.OpenTripMapAttractionResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AttractionMapper implements IAttractionMapper {

    @Autowired
    public CounterManager counterManager;

    @Override
    public List<IAttractionDTO> mapToAttractionDTO(OpenTripMapAttractionResponseDTO openTripMapAttractionResponseDTO) {

        if(openTripMapAttractionResponseDTO == null || openTripMapAttractionResponseDTO.getFeatures() == null) {
            return Collections.emptyList();
        }

        return openTripMapAttractionResponseDTO.getFeatures().stream().map(this::mapToAttractionDTO).toList();
    }

    private IAttractionDTO mapToAttractionDTO(OpenTripMapAttractionResponseDTO.Feature feature) {
        IAttractionDTO attractionDTO = new AttractionDTO();

        if(feature == null || feature.getProperties() == null)  {
            return attractionDTO;
        }

        List<String> categories = Optional.ofNullable(feature.getProperties().getKinds())
                .map(k -> Arrays.stream(k.split(",")).toList())
                .orElseGet(ArrayList::new);

        attractionDTO.setPoiId(counterManager.nextPoiId());
        attractionDTO.setName(feature.getProperties().getName());
        attractionDTO.setCategory(categories);
        //attractionDTO.setActivityType(ActivityType.ATTRACTION);
        attractionDTO.setNote(Constant.EMPTY_NOTE);

        return attractionDTO;
    }
}
