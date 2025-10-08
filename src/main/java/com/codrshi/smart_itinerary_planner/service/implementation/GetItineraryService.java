package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.QueryBuilder;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetItineraryService implements IGetItineraryService {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @Override
    public IItineraryResponseDTO getItinerary(String itineraryId) {
        validationService.validateItineraryId(itineraryId, HttpStatus.BAD_REQUEST);

        Optional<Itinerary> itineraryOpt = itineraryRepository.findByItineraryId(itineraryId);

        return itineraryOpt.map(itinerary -> itineraryMapper.mapToGetItineraryResponseDTO(itinerary))
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY));

    }

    @Override
    public Page<IItineraryResponseDTO> getItineraries(IGetItineraryRequestDTO getItineraryRequestDTO, Pageable pageable) {
        validationService.validateFilterItineraryRequest(getItineraryRequestDTO);

        Query query = QueryBuilder.builder(getItineraryRequestDTO);
        Page<Itinerary> itineraries = itineraryRepository.searchItineraries(query, pageable);

        return itineraries.map(itinerary -> itineraryMapper.mapToGetItineraryResponseDTO(itinerary));
    }
}
