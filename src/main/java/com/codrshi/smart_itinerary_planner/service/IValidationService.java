package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IFilterRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchItineraryRequestDTO;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface IValidationService {

    void validateExternalApiResponse(int totalEvents, int totalAttractions, int weatherDays, int totalDays);

    void validateItineraryId(String itineraryId, HttpStatus httpStatus);

    void validateCreateItineraryEvent(ICreateItineraryEventDTO createItineraryEventDTO);

    void validateFilterItineraryRequest(IFilterRequestDTO filterRequestDTO);

    void validatePatchItineraryRequest(IPatchItineraryRequestDTO patchItineraryRequestDTO);

    void validatePatchData(List<String> fields);
}
