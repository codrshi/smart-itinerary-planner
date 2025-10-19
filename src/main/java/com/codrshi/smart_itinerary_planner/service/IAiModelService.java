package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.implementation.FlattenedActivityDTO;

import java.util.List;

public interface IAiModelService {
    String generateItinerarySummary(List<FlattenedActivityDTO> itineraries);

    String handleItineraryQuery(String query);
}
