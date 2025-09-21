package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;

public interface IDeleteItineraryService {
    void deleteItinerary(String itineraryId);
    IDeleteItineraryResponseDTO deleteItineraries(IDeleteItineraryRequestDTO deleteItineraryRequestDTO);
}
