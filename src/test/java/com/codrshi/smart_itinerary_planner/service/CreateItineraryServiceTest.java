package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.CreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class CreateItineraryServiceTest extends BaseTest {

    @MockitoBean
    private IExternalApiService externalApiService;

    @Autowired
    private ICreateItineraryService createItineraryService;

    @BeforeEach
    public void beforeEach() {
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run(); // run synchronously
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    void givenCreateItineraryService_whenCorrectRequest_ThenOkResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getRequestBody();
        List<IEventDTO> mockedEvents = getJsonObject("CreateItineraryServiceTest/validEvents.json",
                                                     new TypeReference<>() {});
        List<IAttractionDTO> mockedAttractions = getJsonObject("CreateItineraryServiceTest/validAttractions.json",
                                                               new TypeReference<>() {});

        Map<LocalDate, WeatherType> mockedDateToWeatherMap = FactoryUtil.defaultDateToWeatherMap(createItineraryRequestDTO.getTimePeriod());

        when(externalApiService.getTicketmasterEvents(any(), any())).thenReturn(mockedEvents);
        when(externalApiService.getGeoapifyAttractions(anyInt(), any(), anyInt())).thenReturn(mockedAttractions);
        when(externalApiService.getVirtualCrossingWeather(any(), any())).thenReturn(mockedDateToWeatherMap);

        when(itineraryRepository.save(any(Itinerary.class)))
                .thenAnswer(invocation -> {
                    Itinerary itinerary = invocation.getArgument(0);
                    itinerary.setDocId("testDocId");
                    return itinerary;
                });

        ICreateItineraryResponseDTO responseDTO = createItineraryService.createItinerary(createItineraryRequestDTO);

        assertEquals("New York, United States", responseDTO.getDestination() );
        assertTrue(responseDTO.getItineraryId().startsWith(Constant.ITINERARY_ID_PREFIX));
        assertEquals(createItineraryRequestDTO.getTimePeriod(), responseDTO.getTimePeriod());
        assertEquals(2, responseDTO.getAttractionsFound());
        assertEquals(3, responseDTO.getEventsFound());
    }

    @Test
    void givenCreateItineraryService_whenAsyncFails_ThenRuntimeError() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getRequestBody();

        Map<LocalDate, WeatherType> mockedDateToWeatherMap = FactoryUtil.defaultDateToWeatherMap(createItineraryRequestDTO.getTimePeriod());

        when(externalApiService.getTicketmasterEvents(any(), any())).thenThrow(new RuntimeException("Ticketmaster API down"));
        when(externalApiService.getGeoapifyAttractions(anyInt(), any(), anyInt())).thenThrow(new RuntimeException(
                "OSM API down"));
        when(externalApiService.getVirtualCrossingWeather(any(), any())).thenReturn(mockedDateToWeatherMap);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> createItineraryService.createItinerary(createItineraryRequestDTO)
        );

        assertEquals("Ticketmaster API down", ex.getCause().getMessage());
        assertEquals("Ticketmaster API down", ex.getCause().getMessage());
    }

    private ICreateItineraryRequestDTO getRequestBody() {
        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(LocalDate.parse("2025-11-01"));
        timePeriodDTO.setEndDate(LocalDate.parse("2025-11-05"));

        ICreateItineraryRequestDTO createItineraryRequestDTO = new CreateItineraryRequestDTO();
        createItineraryRequestDTO.setCity("New York");
        createItineraryRequestDTO.setCountry("US");
        createItineraryRequestDTO.setTimePeriod(timePeriodDTO);

        return createItineraryRequestDTO;
    }
}
