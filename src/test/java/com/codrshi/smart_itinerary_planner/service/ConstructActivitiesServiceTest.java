package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.ConstructActivitiesService;
import com.codrshi.smart_itinerary_planner.util.CounterManager;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstructActivitiesServiceTest extends BaseTest {

    @Autowired
    private ConstructActivitiesService constructActivitiesService;

    @Autowired
    private CounterManager counterManager;

    @BeforeEach
    void setUp() {
        counterManager.reset();
    }

    @Test
    void givenConstructActivitiesService_whenValidRequest_thenReturnActivities() {
        List<IEventDTO> events = getJsonObject("ConstructActivitiesServiceTest/validEvents_1.json",
                                               new TypeReference<>() {});
        List<IAttractionDTO> attractions = getJsonObject("ConstructActivitiesServiceTest/validAttractions_1.json",
                                                         new TypeReference<>() {});
        Map<LocalDate, WeatherType> dateToWeatherMap = getJsonObject("ConstructActivitiesServiceTest/validDateToWeatherMap_1.json",
                                                                    new TypeReference<>() {});
        List<IActivityDTO> expectedActivities = getJsonObject("ConstructActivitiesServiceTest/expectedActivities_1.json",
                                                             new TypeReference<>() {});

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, attractions, dateToWeatherMap);

        assertEquals(expectedActivities, activities);
    }

    @Test
    void givenConstructActivitiesService_whenNoEvents_thenReturnActivities() {
        List<IAttractionDTO> attractions = getJsonObject("ConstructActivitiesServiceTest/validAttractions_1.json",
                                                         new TypeReference<>() {});
        Map<LocalDate, WeatherType> dateToWeatherMap = getJsonObject("ConstructActivitiesServiceTest/validDateToWeatherMap_1.json",
                                                                     new TypeReference<>() {});
        List<IActivityDTO> expectedActivities = getJsonObject("ConstructActivitiesServiceTest/expectedActivities_withNoEvents.json",
                                                              new TypeReference<>() {});

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(new ArrayList<>(), attractions, dateToWeatherMap);

        assertEquals(expectedActivities, activities);
    }

    @Test
    void givenConstructActivitiesService_whenNoAttractions_thenReturnActivities() {
        List<IEventDTO> events = getJsonObject("ConstructActivitiesServiceTest/validEvents_1.json",
                                               new TypeReference<>() {});
        Map<LocalDate, WeatherType> dateToWeatherMap = getJsonObject("ConstructActivitiesServiceTest/validDateToWeatherMap_1.json",
                                                                     new TypeReference<>() {});
        List<IActivityDTO> expectedActivities = getJsonObject("ConstructActivitiesServiceTest/expectedActivities_withNoAttractions.json",
                                                              new TypeReference<>() {});

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, new ArrayList<>(),
                                                                                       dateToWeatherMap);

        assertEquals(expectedActivities, activities);
    }

    @Test
    void givenConstructActivitiesService_whenBadWeather_thenReturnActivities() {
        List<IEventDTO> events = getJsonObject("ConstructActivitiesServiceTest/validEvents_1.json",
                                               new TypeReference<>() {});
        List<IAttractionDTO> attractions = getJsonObject("ConstructActivitiesServiceTest/validAttractions_1.json",
                                                         new TypeReference<>() {});
        Map<LocalDate, WeatherType> dateToWeatherMap = getJsonObject("ConstructActivitiesServiceTest/validDateToWeatherMap_aboveThresholdRank.json",
                                                                     new TypeReference<>() {});
        List<IActivityDTO> expectedActivities = getJsonObject("ConstructActivitiesServiceTest/expectedActivities_withWeatherRankAboveThreshold.json",
                                                              new TypeReference<>() {});

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, attractions,
                                                                                       dateToWeatherMap);

        assertEquals(expectedActivities, activities);
    }

    @Test
    void givenConstructActivitiesService_whenBadWeatherAndNoEvents_thenEmptyActivities() {
        List<IAttractionDTO> attractions = getJsonObject("ConstructActivitiesServiceTest/validAttractions_1.json",
                                                         new TypeReference<>() {});
        Map<LocalDate, WeatherType> dateToWeatherMap = getJsonObject("ConstructActivitiesServiceTest/validDateToWeatherMap_aboveThresholdRank.json",
                                                                     new TypeReference<>() {});

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(new ArrayList<>(), attractions,
                                                                                       dateToWeatherMap);

        assertTrue(activities.isEmpty());
    }
}
