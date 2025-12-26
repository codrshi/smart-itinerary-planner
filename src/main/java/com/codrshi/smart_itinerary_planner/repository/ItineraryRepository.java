package com.codrshi.smart_itinerary_planner.repository;

import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItineraryRepository extends MongoRepository<Itinerary, String>, ItineraryRepositoryCustomQuery {
    Optional<Itinerary> findByItineraryId(String itineraryId);
    boolean existsByItineraryId(String itineraryId);
    void deleteByItineraryId(String itineraryId);
}
