package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
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
    private LocalDate date;
    private ActivityType activityType;
    private List<String> kinds;
    private String note;

    public AttractionDTO(IAttractionDTO other) {
        this.poiId = other.getPoiId();
        this.name = other.getName();
        this.date = other.getDate();
        this.activityType = other.getActivityType();
        this.kinds = other.getKinds() != null
                ? new ArrayList<>(other.getKinds())
                : null;
        this.note = other.getNote();
    }
}
