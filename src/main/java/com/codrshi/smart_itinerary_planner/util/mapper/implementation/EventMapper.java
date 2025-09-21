package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TicketMasterEventResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class EventMapper implements IEventMapper {

    @Autowired
    public CounterManager counterManager;

    public List<IEventDTO> mapToEventDTO(TicketMasterEventResponseDTO ticketMasterEventResponseDTO) {
        if(ticketMasterEventResponseDTO==null || ticketMasterEventResponseDTO.get_embedded() == null || ticketMasterEventResponseDTO.get_embedded().getEvents() == null) {
            return Collections.emptyList();
        }

        List<TicketMasterEventResponseDTO.Event> events = ticketMasterEventResponseDTO.get_embedded().getEvents();

        return events.stream().map(this::mapToEventDTO).toList();
    }

    public IEventDTO mapToEventDTO(TicketMasterEventResponseDTO.Event event) {
        IEventDTO eventDTO = new EventDTO();

        if(event==null)
            return eventDTO;

        TicketMasterEventResponseDTO.Venue rawVenue = event.get_embedded().getVenues().get(0);
        TicketMasterEventResponseDTO.Classification rawClassification = event.getClassifications().get(0);

        LocalDate date = event.getDates().getStart().getLocalDate();
        String venue = rawVenue.getName() + ", " + rawVenue.getAddress().getLine1();
        String category = String.join(" / ", rawClassification.getSegment().getName(),
                                      rawClassification.getGenre().getName(),
                                      rawClassification.getSubGenre().getName());

        eventDTO.setPoiId(counterManager.nextPoiId());
        eventDTO.setName(event.getName());
        eventDTO.setCategory(category);
        eventDTO.setVenue(venue);
        eventDTO.setDate(date);
        eventDTO.setFamilyFriendly(rawClassification.isFamily());
        eventDTO.setActivityType(ActivityType.EVENT);
        eventDTO.setNote(Constant.EMPTY_NOTE);

        return eventDTO;
    }
}
