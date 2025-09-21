package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItineraryRequestDTO implements ICreateItineraryRequestDTO {
    @NotBlank(message = "city is either null or empty")
    @ValidLocation(message = "city contains invalid character(s)")
    private String city;
    @NotBlank(message = "country is either null or empty")
    @ValidLocation(message = "country contains invalid character(s)")
    private String country;
    @NotNull(message = "timePeriod is null")
    @Valid
    private ITimePeriodDTO timePeriod;
}
