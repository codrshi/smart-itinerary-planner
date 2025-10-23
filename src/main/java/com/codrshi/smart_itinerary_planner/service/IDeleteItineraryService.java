package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.implementation.response.ApiResponseWrapper;
import com.codrshi.smart_itinerary_planner.dto.request.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;

public interface IDeleteItineraryService {
    void deleteItinerary(String itineraryId);
    IDeleteItineraryResponseDTO deleteItineraries(IDeleteItineraryRequestDTO deleteItineraryRequestDTO);
}
