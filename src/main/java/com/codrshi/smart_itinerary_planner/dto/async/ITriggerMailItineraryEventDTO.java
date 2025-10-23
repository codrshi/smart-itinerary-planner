package com.codrshi.smart_itinerary_planner.dto.async;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;

public interface ITriggerMailItineraryEventDTO {
    String getItineraryId();

    void setItineraryId(String itineraryId);

    String getUsername();

    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    String getDestination();

    void setDestination(String destination);

    ITimePeriodDTO getTimePeriod();

    void setTimePeriod(ITimePeriodDTO timePeriod);

    String getSummarizedActivities();

    void setSummarizedActivities(String summarizedActivities);
}
