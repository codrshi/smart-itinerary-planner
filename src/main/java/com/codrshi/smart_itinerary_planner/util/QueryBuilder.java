package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.dto.IFilterRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IGetItineraryRequestDTO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class QueryBuilder {
    private QueryBuilder() {}

    public static final String CITY = "location.city";
    public static final String START_DATE = "timePeriod.startDate";
    public static final String END_DATE = "timePeriod.endDate";
    public static final String COUNTRY = "location.country";
    public static final String COUNTRY_CODE = "location.countryCode";

    public static Query builder(IFilterRequestDTO filterRequestDTO) {
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
