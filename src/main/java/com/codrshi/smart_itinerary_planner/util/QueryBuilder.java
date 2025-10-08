package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.dto.request.IFilterRequestDTO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class QueryBuilder {
    private QueryBuilder() {}

    private static final String CITY = "location.city";
    private static final String START_DATE = "timePeriod.startDate";
    private static final String END_DATE = "timePeriod.endDate";
    private static final String COUNTRY = "location.country";
    private static final String COUNTRY_CODE = "location.countryCode";
    private static final String ITINERARY_ID = "itineraryId";
    private static final String USER_REF_ID = "userRef";

    public static Query builder(String itineraryId, Long version) {
        String userRef = RequestContext.getCurrentContext().getUsername();
        Query query = new Query();
        query.addCriteria(Criteria.where(ITINERARY_ID).is(itineraryId).and(USER_REF_ID).is(userRef).and(Constant.VERSION).is(version));

        return query;
    }

    public static Query builder(IFilterRequestDTO filterRequestDTO) {
        String userRef = RequestContext.getCurrentContext().getUsername();
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        String city = filterRequestDTO.getCity();
        String country = filterRequestDTO.getCountry();
        LocalDate startDate = filterRequestDTO.getStartDate();
        LocalDate endDate = filterRequestDTO.getEndDate();
        DateRangeCriteria dateRangeCriteria = filterRequestDTO.getDateRangeCriteria();

        if(city != null) {
            criteriaList.add(Criteria.where(CITY).regex("^" + Pattern.quote(city) + "$", "i"));
        }

        if(country != null) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where(COUNTRY).regex("^" + Pattern.quote(country) + "$", "i"),
                    Criteria.where(COUNTRY_CODE).regex("^" + Pattern.quote(country) + "$", "i")
            ));
        }

        if(startDate != null || endDate != null) {
            criteriaList.add(addDateRangeCriteria(dateRangeCriteria, startDate, endDate));
        }

        if(!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        query.addCriteria(Criteria.where(USER_REF_ID).is(userRef));

        return query;
    }

    private static Criteria addDateRangeCriteria( DateRangeCriteria dateRangeCriteria,
                                             LocalDate startDate,
                                             LocalDate endDate){
        DateRangeCriteria criteria = Optional.ofNullable(dateRangeCriteria).orElse(DateRangeCriteria.WITHIN_DATE_RANGE);

        return switch (criteria) {
            case WITHIN_DATE_RANGE -> {
                if (startDate != null && endDate != null) {
                    yield new Criteria().andOperator(Criteria.where(START_DATE).gte(startDate),
                                                      Criteria.where(END_DATE).lte(endDate));
                } else if (startDate != null) {
                    yield Criteria.where(START_DATE).gte(startDate);
                } else {
                    yield Criteria.where(END_DATE).lte(endDate);
                }
            }
            case SAME_DATE_RANGE ->  new Criteria().andOperator(Criteria.where(START_DATE).is(startDate), Criteria.where(END_DATE).is(endDate));
            case CONTAINS_DATE_RANGE -> new Criteria().andOperator(Criteria.where(START_DATE).lte(startDate), Criteria.where(END_DATE).gte(endDate));
            case INTERSECTS_DATE_RANGE -> new Criteria().andOperator(Criteria.where(START_DATE).lte(endDate), Criteria.where(END_DATE).gte(startDate));
        };
    }
}
