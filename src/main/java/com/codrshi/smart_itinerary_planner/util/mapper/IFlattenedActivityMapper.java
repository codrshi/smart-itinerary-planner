package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.FlattenedActivityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface IFlattenedActivityMapper {

    List<FlattenedActivityDTO> mapToFlattenedActivityList(List<IActivityDTO> activity);

    @Mapping(target = "weather", expression = "java(activity.getWeatherType().getLabel())")
    FlattenedActivityDTO mapToFlattenedActivity(IActivityDTO activity);

    List<FlattenedActivityDTO.FlattenedPointOfInterestDTO> mapToFlattenedPointOfInterestList(List<IPointOfInterestDTO> pointOfInterests);

    @Mapping(target = "poiNote", expression = "java(pointOfInterests.getNote())")
    @Mapping(target = "category", expression = "java(pointOfInterests.getCategory().stream().collect(Collectors" +
            ".joining(\", \")))")
    FlattenedActivityDTO.FlattenedPointOfInterestDTO mapToFlattenedPointOfInterest(IPointOfInterestDTO pointOfInterests);
}
