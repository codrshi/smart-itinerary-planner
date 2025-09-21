package com.codrshi.smart_itinerary_planner.repository.implementation;

import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepositoryCustomQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItineraryRepositoryCustomQueryImpl implements ItineraryRepositoryCustomQuery {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Itinerary> searchItineraries(Query query, Pageable pageable) {
        long total = mongoTemplate.count(query, Itinerary.class);
        query.with(pageable);

        List<Itinerary> itineraries = mongoTemplate.find(query, Itinerary.class);

        return new PageImpl<>(itineraries, pageable, total);
    }

    @Override
    public List<Itinerary> deleteItineraries(Query query) {
        return mongoTemplate.findAllAndRemove(query, Itinerary.class);
    }
}
