package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ItineraryStatus;
import lombok.Data;

@Data
public class CreateItineraryResponseDTO implements ICreateItineraryResponseDTO {
    private String destination;
    private ITimePeriodDTO timePeriodDTO;
    private String itineraryId;
    private ItineraryStatus itineraryStatus;
}
