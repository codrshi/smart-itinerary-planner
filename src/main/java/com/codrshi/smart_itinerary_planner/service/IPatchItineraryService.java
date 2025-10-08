package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;

public interface IPatchItineraryService {
    IItineraryResponseDTO patchItinerary(String itineraryId, IPatchItineraryRequestDTO patchItineraryRequestDTO);
}
