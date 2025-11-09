package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DateRangeCriteriaConverter implements Converter<String, DateRangeCriteria> {
    @Override
    public DateRangeCriteria convert(String value) {
        return DateRangeCriteria.fromString(value);
    }
}
