package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class EventDTO implements IEventDTO {
    private String poiId;
    private String name;
    private LocalDate date;
    private ActivityType activityType;
    private String venue;
    private boolean isFamilyFriendly;
    private String category;
    private String note;

    public EventDTO(IEventDTO other) {
        this.poiId = other.getPoiId();
        this.name = other.getName();
        this.date = other.getDate();
        this.activityType = other.getActivityType();
        this.venue = other.getVenue();
        this.isFamilyFriendly = other.isFamilyFriendly();
        this.category = other.getCategory();
        this.note = other.getNote();
    }
}
