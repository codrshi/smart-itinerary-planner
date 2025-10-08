package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.entity.ItineraryHistory;

public interface IItineraryHistoryMapper {
    ItineraryHistory map(ICreateItineraryRequestDTO createItineraryEventDTO);
}
