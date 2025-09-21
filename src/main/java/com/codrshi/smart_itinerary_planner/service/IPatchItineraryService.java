package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchItineraryRequestDTO;

public interface IPatchItineraryService {
    IItineraryResponseDTO patchItinerary(String itineraryId, IPatchItineraryRequestDTO patchItineraryRequestDTO);
}
