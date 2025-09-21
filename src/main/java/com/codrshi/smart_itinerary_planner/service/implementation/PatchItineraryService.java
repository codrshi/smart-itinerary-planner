package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IPatchItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import com.codrshi.smart_itinerary_planner.util.PatchDataRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.codrshi.smart_itinerary_planner.common.enums.PatchOperation.UPDATE_NOTE;

@Service
public class PatchItineraryService implements IPatchItineraryService {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Override
    public IItineraryResponseDTO patchItinerary(String itineraryId, IPatchItineraryRequestDTO patchItineraryRequestDTO) {
        System.out.println(patchItineraryRequestDTO);
        validationService.validateItineraryId(itineraryId, HttpStatus.BAD_REQUEST);
        validationService.validatePatchItineraryRequest(patchItineraryRequestDTO);

        Itinerary itinerary = itineraryRepository.findByItineraryId(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY));

        Itinerary patchedItinerary = dispatch(patchItineraryRequestDTO, itinerary);

        //save itinerary
        return new PatchItineraryResponseDTO();
    }

    private Itinerary dispatch(IPatchItineraryRequestDTO patchItineraryRequestDTO, Itinerary itinerary) {
        String patchOperation = patchItineraryRequestDTO.getPatchOperation().getValue();
        PatchHandler patchHandler = PatchDataRegistry.getHandlerObject(patchOperation);

        List<IActivityDTO> patchedActivities = patchHandler.handle(itinerary.getActivities(),
                                                                   patchItineraryRequestDTO.getPatchData());

        itinerary.setActivities(patchedActivities);
        return itinerary;
    }
}
