package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.LocationDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = EventDTO.class)
public interface IEventDTO extends IPointOfInterestDTO {

    String getVenue();

    void setVenue(String venue);

    boolean isFamilyFriendly();

    void setFamilyFriendly(boolean familyFriendly);

    String getCategory();

    void setCategory(String category);
}
