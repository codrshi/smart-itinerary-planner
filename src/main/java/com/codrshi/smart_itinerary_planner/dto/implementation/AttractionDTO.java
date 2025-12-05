package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AttractionDTO implements IAttractionDTO {
    private String poiId;
    private String name;
    //TODO: remove date field
    private LocalDate date;
    private List<String> category;
    private String address;
    private String note;

    @JsonIgnore
    public ActivityType getActivityType() {
        return ActivityType.ATTRACTION;
    }
}
