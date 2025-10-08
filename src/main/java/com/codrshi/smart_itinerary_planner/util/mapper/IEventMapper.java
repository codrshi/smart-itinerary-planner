package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.TicketMasterEventResponseDTO;

import java.util.List;

public interface IEventMapper {
    List<IEventDTO> mapToEventDTO(TicketMasterEventResponseDTO ticketMasterEventResponseDTO);
}
