package com.codrshi.smart_itinerary_planner.repository.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepositoryCustomQuery;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
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

    @Override
    public void updateActivities(Query query, List<IActivityDTO> activities) {
        Update update = new Update().set(Constant.ACTIVITIES, activities)
                            .set("updatedAt", LocalDate.now())
                            .set("updatedBy", RequestContext.getCurrentContext().getUsername())
                            .inc(Constant.VERSION, 1);

        UpdateResult result = mongoTemplate.updateFirst(query, update, Itinerary.class);

        if(result.getModifiedCount() == 0) {
            throw new OptimisticLockingFailureException("Itinerary was modified by another process.");
        }
    }
}
