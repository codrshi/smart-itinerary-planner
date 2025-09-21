package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.ItineraryStatus;

public interface ICreateItineraryResponseDTO {
    String getDestination();
    void setDestination(String destination);

    String getItineraryId();
    void setItineraryId(String itineraryId);

    ItineraryStatus getItineraryStatus();
    void setItineraryStatus(ItineraryStatus itineraryStatus);

    ITimePeriodDTO getTimePeriodDTO();
    void setTimePeriodDTO(ITimePeriodDTO timePeriodDTO);
}
