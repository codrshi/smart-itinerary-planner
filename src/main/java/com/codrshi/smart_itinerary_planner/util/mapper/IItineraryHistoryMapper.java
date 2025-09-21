package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.entity.ItineraryHistory;

public interface IItineraryHistoryMapper {
    ItineraryHistory map(ICreateItineraryRequestDTO createItineraryEventDTO);
}
