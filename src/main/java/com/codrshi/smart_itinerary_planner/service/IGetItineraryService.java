package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IGetItineraryService {
    IItineraryResponseDTO getItinerary(String itineraryId);
    Page<IItineraryResponseDTO> getItineraries(IGetItineraryRequestDTO getItineraryRequestDTO, Pageable pageable);

}
