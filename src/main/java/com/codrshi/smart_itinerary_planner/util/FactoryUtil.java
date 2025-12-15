package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class FactoryUtil {

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Autowired
    private CounterManager counterManager;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<LocalDate, IActivityDTO> initializeDateToActivityMap(Map<LocalDate, WeatherType> dateToWeatherMap) {
        return dateToWeatherMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRank() < itineraryProperties.getWeatherRankThreshold()).collect(
                        Collectors.toMap(Map.Entry::getKey,
                                         entry -> ActivityDTO.builder()
                                                                                .activityId(counterManager.nextActivityId())
                                                                                .activityDate(entry.getKey())
                                                                                .weatherType(entry.getValue())
                                                                                .pointOfInterests(new ArrayList<>())
                                                                                .build())
                                        );
    }

    public static PriorityQueue<IActivityDTO> initializeDateToActivityMinHeap() {
        return new PriorityQueue<>(
                        Comparator.comparingInt((IActivityDTO activity) -> activity.getWeatherType().getRank())
                                .thenComparingInt(activity -> activity.getPointOfInterests().size()));

    }

    public static Map<LocalDate, WeatherType> defaultDateToWeatherMap(ITimePeriodDTO timePeriodDTO) {
        LocalDate startDate = timePeriodDTO.getStartDate();
        LocalDate endDate = timePeriodDTO.getEndDate();

        return startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toMap(date -> date, date -> WeatherType.TYPE_0));
    }

    public <T, R extends T> T copy(T object, Class<R> clazz) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(object), clazz);
    }
}
