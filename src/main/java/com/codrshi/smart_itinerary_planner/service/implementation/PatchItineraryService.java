package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IPatchItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.QueryBuilder;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import com.codrshi.smart_itinerary_planner.util.patch.PatchHandlerRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PatchItineraryService implements IPatchItineraryService {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private FactoryUtil factoryUtil;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @Autowired
    private PatchHandlerRegistry patchHandlerRegistry;

    @Override
    public IItineraryResponseDTO patchItinerary(String itineraryId, IPatchItineraryRequestDTO patchItineraryRequestDTO) {

        validationService.validateItineraryId(itineraryId, HttpStatus.BAD_REQUEST);
        validationService.validatePatchItineraryRequest(patchItineraryRequestDTO);

        Itinerary itinerary = itineraryRepository.findByItineraryId(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY));

        log.debug("Found itinerary with itineraryId = {}: {}", itineraryId, itinerary);

        List<IActivityDTO> patchedActivities = dispatch(patchItineraryRequestDTO, itinerary);
        log.debug("Patched activities: {}", patchedActivities);

        updateItineraryInRepository(patchedActivities, itineraryId, itinerary.getVersion());

        return itineraryMapper.mapToPatchItineraryResponseDTO(itinerary, patchedActivities);
    }

    private void updateItineraryInRepository(List<IActivityDTO> patchedActivities, String itineraryId, Long version) {
        Query query = QueryBuilder.builder(itineraryId, version);

        log.debug("Updating itinerary with itineraryId = {} with query = {}", itineraryId, query);
        itineraryRepository.updateActivities(query, patchedActivities);
    }

    private List<IActivityDTO> dispatch(IPatchItineraryRequestDTO patchItineraryRequestDTO, Itinerary itinerary) {
        Itinerary patchedItinerary;
        try {
            patchedItinerary = factoryUtil.copy(itinerary, Itinerary.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to prepare copy of itinerary before patching", e);
            throw new RuntimeException(e);
        }

        String patchOperation = patchItineraryRequestDTO.getPatchOperation().getValue();
        PatchHandler patchHandler = patchHandlerRegistry.getHandler(patchOperation);

        log.debug("Dispatching patch operation = {} to handler {}", patchOperation, patchHandler.getClass().getSimpleName());

        return patchHandler.handle(patchedItinerary.getActivities(), patchItineraryRequestDTO.getPatchData());

    }
}
