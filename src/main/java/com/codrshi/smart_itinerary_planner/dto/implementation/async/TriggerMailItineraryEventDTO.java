package com.codrshi.smart_itinerary_planner.dto.implementation.async;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.async.ITriggerMailItineraryEventDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriggerMailItineraryEventDTO implements ITriggerMailItineraryEventDTO {
    private String itineraryId;
    private String username;
    private String email;
    private String destination;
    private ITimePeriodDTO timePeriod;
    private String summarizedActivities;
}