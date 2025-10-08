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
import com.codrshi.smart_itinerary_planner.util.patch.PatchDataRegistry;
import com.codrshi.smart_itinerary_planner.util.QueryBuilder;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatchItineraryService implements IPatchItineraryService {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private FactoryUtil factoryUtil;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @Override
    public IItineraryResponseDTO patchItinerary(String itineraryId, IPatchItineraryRequestDTO patchItineraryRequestDTO) {
        System.out.println(patchItineraryRequestDTO);
        validationService.validateItineraryId(itineraryId, HttpStatus.BAD_REQUEST);
        validationService.validatePatchItineraryRequest(patchItineraryRequestDTO);

        Itinerary itinerary = itineraryRepository.findByItineraryId(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY));

        List<IActivityDTO> patchedActivities = dispatch(patchItineraryRequestDTO, itinerary);
        updateItineraryInRepository(patchedActivities, itineraryId, itinerary.getVersion());
        
        //save itinerary
        return itineraryMapper.mapToPatchItineraryResponseDTO(itinerary, patchedActivities);
    }

    private void updateItineraryInRepository(List<IActivityDTO> patchedActivities, String itineraryId, Long version) {
        Query query = QueryBuilder.builder(itineraryId, version);
        itineraryRepository.updateActivities(query, patchedActivities);
    }

    private List<IActivityDTO> dispatch(IPatchItineraryRequestDTO patchItineraryRequestDTO, Itinerary itinerary) {
        Itinerary patchedItinerary;
        try {
            patchedItinerary = factoryUtil.copy(itinerary, Itinerary.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String patchOperation = patchItineraryRequestDTO.getPatchOperation().getValue();
        PatchHandler patchHandler = PatchDataRegistry.getHandlerObject(patchOperation);

        return patchHandler.handle(patchedItinerary.getActivities(),
                                                                   patchItineraryRequestDTO.getPatchData());

    }
}
