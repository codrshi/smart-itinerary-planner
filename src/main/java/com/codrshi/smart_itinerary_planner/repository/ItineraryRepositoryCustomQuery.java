package com.codrshi.smart_itinerary_planner.repository;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface ItineraryRepositoryCustomQuery {
    Page<Itinerary> searchItineraries(Query query, Pageable pageable);
    List<Itinerary> deleteItineraries(Query query);
    void updateActivities(Query query, List<IActivityDTO> activities);
}
