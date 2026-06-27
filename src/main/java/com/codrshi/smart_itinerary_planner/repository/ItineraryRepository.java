package com.codrshi.smart_itinerary_planner.repository;

import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItineraryRepository extends MongoRepository<Itinerary, String>, ItineraryRepositoryCustomQuery {
    Optional<Itinerary> findByItineraryIdAndUserRef(String itineraryId, String userRef);
    boolean existsByItineraryIdAndUserRef(String itineraryId, String userRef);
    void deleteByItineraryIdAndUserRef(String itineraryId, String userRef);
}
