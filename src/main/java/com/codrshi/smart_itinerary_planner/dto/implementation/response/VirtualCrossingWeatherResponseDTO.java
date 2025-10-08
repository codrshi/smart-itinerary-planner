package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import lombok.Data;

import java.util.List;

@Data
public class VirtualCrossingWeatherResponseDTO {
    private List<Days> days;

    @Data
    public static class Days {
        private String datetime;
        private String conditions;
    }
}
