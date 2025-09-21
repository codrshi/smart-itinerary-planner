package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;

public interface ICreateItineraryService {
    ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryEventDTO);
}
