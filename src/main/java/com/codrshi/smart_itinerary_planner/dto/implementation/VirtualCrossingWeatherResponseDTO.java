package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
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
