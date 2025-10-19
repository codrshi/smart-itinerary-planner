package com.codrshi.smart_itinerary_planner.dto.implementation;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FlattenedActivityDTO {

    private LocalDate activityDate;
    private String weather;
    private String activityNote;
    private List<FlattenedPointOfInterestDTO> pointOfInterests;

    @Data
    public static class FlattenedPointOfInterestDTO {
        private String name;
        private String poiNote;
        private String category;
    }
}
