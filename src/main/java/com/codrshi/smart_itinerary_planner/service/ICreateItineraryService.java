package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;

public interface ICreateItineraryService {
    ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryEventDTO);
}
