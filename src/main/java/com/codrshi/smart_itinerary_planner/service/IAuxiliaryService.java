package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;

public interface IAuxiliaryService {
    Object query(IAuxiliaryRequestDTO auxiliaryRequestDTO);

    void mailItinerary(String itineraryId);
}
