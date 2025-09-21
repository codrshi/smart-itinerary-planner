package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.service.IConstructActivitiesService;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class ConstructActivitiesService implements IConstructActivitiesService {

    @Autowired
    private FactoryUtil factoryUtil;

    @Autowired
    private ActivityUtil activityUtil;

    @Autowired
    private CounterManager counterManager;

    @Override
    public List<IActivityDTO> constructActivities(List<IEventDTO> events, List<IAttractionDTO> attractions,
                                                   Map<LocalDate, WeatherType> dateToWeatherMap) {

        List<IActivityDTO> activities = new ArrayList<>();
        Map<LocalDate, IActivityDTO> dateToActivityMap = factoryUtil.initializeDateToActivityMap(dateToWeatherMap);

        populateEvents(events, dateToActivityMap, dateToWeatherMap);
        populateAttractions(attractions, activities, dateToActivityMap.values().stream().toList(), events.size());

        activities.sort(Comparator.comparing(activityDTO -> activityDTO.getActivityDate()));

        return activities;
    }

    private void populateAttractions(List<IAttractionDTO> attractions,
                                     List<IActivityDTO> activities,
                                     List<IActivityDTO> rawActivities,
                                     int totalEvents) {

        int poiPerDay = activityUtil.calculatePoiPerDay(rawActivities, totalEvents, attractions.size());
        PriorityQueue<IActivityDTO> activityMinHeap = FactoryUtil.initializeDateToActivityMinHeap();
        activityMinHeap.addAll(rawActivities);

        // TODO: Check if cooldown queue needed to avoid clustering of attractions in best weathers
        attractions.forEach(attraction -> {
            IActivityDTO activityDTO = activityMinHeap.poll();
            List<IPointOfInterestDTO> poiList = activityDTO.getPointOfInterests();

            if(poiList.size() < poiPerDay) {
                poiList.add(attraction);
                activityMinHeap.add(activityDTO);
            }
            else {
                activities.add(activityDTO);
            }
        });

        activities.addAll(activityMinHeap.stream().filter(activityDTO -> activityDTO.getPointOfInterests().size() > 0)
                                  .collect(Collectors.toList()));
    }

    private void populateEvents(List<IEventDTO> events, Map<LocalDate, IActivityDTO> dateToActivityMap, Map<LocalDate, WeatherType> dateToWeatherMap) {
        events.forEach(event -> {
            LocalDate date = event.getDate();
            IActivityDTO activityDTO = dateToActivityMap.getOrDefault(date, ActivityDTO.builder()
                    .activityId(counterManager.nextActivityId())
                    .activityDate(date)
                    .weatherType(dateToWeatherMap.get(date))
                    .build());
            List<IPointOfInterestDTO> poiList = activityDTO.getPointOfInterests();

            poiList.add(event);
            activityDTO.setPointOfInterests(poiList);

            dateToActivityMap.put(date, activityDTO);
        });
    }
}
