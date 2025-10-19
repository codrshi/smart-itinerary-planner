package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.TicketMasterEventResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ActivityType;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if (event == null) return eventDTO;

        String venue = Optional.ofNullable(event.get_embedded())
                .map(TicketMasterEventResponseDTO.EmbeddedVenue::getVenues)
                .filter(v -> !v.isEmpty())
                .map(v -> v.get(0))
                .filter(v -> v.getAddress() != null)
                .map(v -> v.getName() + ", " + v.getAddress().getLine1())
                .orElse(null);

        TicketMasterEventResponseDTO.Classification classification = Optional.ofNullable(event.getClassifications())
                .filter(c -> !c.isEmpty())
                .map(c -> c.get(0))
                .orElse(null);

        boolean isFamilyFriendly = Optional.ofNullable(classification)
                .map(TicketMasterEventResponseDTO.Classification::isFamily)
                .orElse(false);

        List<String> categories = Stream.of(
                        Optional.ofNullable(classification).map(TicketMasterEventResponseDTO.Classification::getSegment).orElse(null),
                        Optional.ofNullable(classification).map(TicketMasterEventResponseDTO.Classification::getGenre).orElse(null),
                        Optional.ofNullable(classification).map(TicketMasterEventResponseDTO.Classification::getSubGenre).orElse(null)
                )
                .filter(Objects::nonNull)
                .map(c -> c.getName())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //TODO: support for dateTime
        LocalDate date = Optional.ofNullable(event.getDates())
                .map(TicketMasterEventResponseDTO.Dates::getStart)
                .map(TicketMasterEventResponseDTO.Start::getDateTime)
                .map(l -> l.toLocalDate())
                .orElse(null);

        // Build DTO
        eventDTO.setPoiId(counterManager.nextPoiId());
        eventDTO.setName(event.getName());
        eventDTO.setCategory(categories);
        eventDTO.setVenue(venue);
        eventDTO.setDate(date);
        eventDTO.setFamilyFriendly(isFamilyFriendly);
        eventDTO.setActivityType(ActivityType.EVENT);
        eventDTO.setNote(Constant.EMPTY_NOTE);

        return eventDTO;
    }
}
