package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDTO implements IEventDTO {
    private String poiId;
    private String name;
    private LocalDate date;
    private String venue;
    private boolean isFamilyFriendly;
    private List<String> category;
    private String note;

    @JsonIgnore
    public ActivityType getActivityType() {
        return ActivityType.EVENT;
    }
}
