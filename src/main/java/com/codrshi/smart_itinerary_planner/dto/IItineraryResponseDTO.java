package com.codrshi.smart_itinerary_planner.dto;

import java.util.List;

public interface IItineraryResponseDTO {
    String getItineraryId();

    void setItineraryId(String itineraryId);

    String getDestination();

    void setDestination(String destination);

    ITimePeriodDTO getTimePeriod();

    void setTimePeriod(ITimePeriodDTO timePeriod);

    int getTotalDays();

    void setTotalDays(int totalDays);

    List<IActivityDTO> getActivities();

    void setActivities(List<IActivityDTO> activities);

    IUserRefDTO getUserRef();

    void setUserRef(IUserRefDTO userRef);
}
