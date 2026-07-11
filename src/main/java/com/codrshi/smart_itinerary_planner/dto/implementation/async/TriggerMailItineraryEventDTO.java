package com.codrshi.smart_itinerary_planner.dto.implementation.async;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.async.ITriggerMailItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.Masked;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class TriggerMailItineraryEventDTO implements ITriggerMailItineraryEventDTO {
    private String itineraryId;
    private String username;
    @Masked
    @ToString.Exclude
    private String email;
    private String destination;
    private ITimePeriodDTO timePeriod;
    private String summarizedActivities;
}