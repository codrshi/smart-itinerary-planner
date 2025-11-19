package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.dto.implementation.AttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "activityType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventDTO.class, name = "event"),
        @JsonSubTypes.Type(value = AttractionDTO.class, name = "attraction")
})
public interface IPointOfInterestDTO {
    String getPoiId();
    void setPoiId(String poiId);

    String getName();
    void setName(String name);

    LocalDate getDate();
    void setDate(LocalDate date);

    ActivityType getActivityType();

    String getNote();
    void setNote(String note);

    List<String> getCategory();
    void setCategory(List<String> category);
}
