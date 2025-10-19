package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.async.event.TriggerMailItineraryEvent;
import com.codrshi.smart_itinerary_planner.dto.ITriggerMailItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.FlattenedActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TriggerMailItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.service.IAuxiliaryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.mapper.IFlattenedActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AuxiliaryService implements IAuxiliaryService {

    @Autowired
    private AiModelService aiModelService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private IGetItineraryService getItineraryService;

    @Autowired
    private IFlattenedActivityMapper flattenedItineraryMapper;

    @Override
    public Object query(IAuxiliaryRequestDTO auxiliaryRequestDTO) {
        return aiModelService.handleItineraryQuery(auxiliaryRequestDTO.getQuery());
    }

    @Override
    public void mailItinerary(String itineraryId) {
        IItineraryResponseDTO itineraryResponseDTO = getItineraryService.getItinerary(itineraryId);

        String username = RequestContext.getCurrentContext().getUsername();
        String email = RequestContext.getCurrentContext().getEmail();
        log.debug("Username: {}, Email: {}", username, email);

        List<FlattenedActivityDTO> flattenedActivities =
                flattenedItineraryMapper.mapToFlattenedActivityList(itineraryResponseDTO.getActivities());
        log.debug("Prepared flattened activities to feed to AI model: {}", flattenedActivities);

        String summarizedActivities = aiModelService.generateItinerarySummary(flattenedActivities);

        log.trace("Preparing TriggerMailItineraryEventDTO");
        ITriggerMailItineraryEventDTO triggerMailItineraryEventDTO = TriggerMailItineraryEventDTO.builder()
                .itineraryId(itineraryId)
                .username(username)
                .email(email)
                .destination(itineraryResponseDTO.getDestination())
                .timePeriod(itineraryResponseDTO.getTimePeriod())
                .summarizedActivities(summarizedActivities)
                .build();

        publisher.publishEvent(new TriggerMailItineraryEvent(this, triggerMailItineraryEventDTO));
        log.debug("TriggerMailItineraryEvent published successfully with body = {}", triggerMailItineraryEventDTO);
    }
}
