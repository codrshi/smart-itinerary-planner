package com.codrshi.smart_itinerary_planner.dto.response;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;

import java.util.List;

public interface ICreateItineraryResponseDTO {
    String getDestination();
    void setDestination(String destination);

    String getItineraryId();
    void setItineraryId(String itineraryId);

    int getEventsFound();
    void setEventsFound(int eventsFound);

    int getAttractionsFound();
    void setAttractionsFound(int attractionsFound);

    ITimePeriodDTO getTimePeriodDTO();
    void setTimePeriodDTO(ITimePeriodDTO timePeriodDTO);
}
