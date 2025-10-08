package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.dto.request.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidLocation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeleteItineraryRequestDTO implements IDeleteItineraryRequestDTO {
    @ValidLocation(message = "city contains invalid character(s)")
    private String city;
    @ValidLocation(message = "country contains invalid character(s)")
    private String country;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private DateRangeCriteria dateRangeCriteria;
}
