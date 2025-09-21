package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IDeleteItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.QueryBuilder;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteItineraryService implements IDeleteItineraryService {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @Override
    public void deleteItinerary(String itineraryId) {

        validationService.validateItineraryId(itineraryId, HttpStatus.BAD_REQUEST);

        if(itineraryRepository.existsByItineraryId(itineraryId)) {
            itineraryRepository.deleteByItineraryId(itineraryId);
        } else {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY);
        }

    }

    @Override
    public IDeleteItineraryResponseDTO deleteItineraries(IDeleteItineraryRequestDTO deleteItineraryRequestDTO) {

        validationService.validateFilterItineraryRequest(deleteItineraryRequestDTO);

        Query query = QueryBuilder.builder(deleteItineraryRequestDTO);
        List<Itinerary> deletedItineraries = itineraryRepository.deleteItineraries(query);
        List<String> itineraryIds = deletedItineraries.stream().map(Itinerary::getItineraryId).toList();

        return itineraryMapper.mapToDeleteItineraryResponseDTO(itineraryIds, false);
    }
}
