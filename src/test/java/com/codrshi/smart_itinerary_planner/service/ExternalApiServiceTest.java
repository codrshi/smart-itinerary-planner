package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.service.implementation.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;


public class ExternalApiServiceTest extends BaseTest{

    @Autowired
    private ExternalApiService externalApiService;

//    @Test
//    void givenExternalApiService_whenGetTicketmasterEvents_thenCorrectUrl() {
//
//        String expectedUrl = "https://app.ticketmaster.com/discovery/v2/events.json?" +
//                "apikey=dummyKey&city=Paris&country=FR&" +
//                "startDateTime=2024-01-01T00:00:00Z&endDateTime=2024-01-05T23:59:59Z";
//
//        ITimePeriodDTO timePeriod = new TimePeriodDTO();
//        timePeriod.setStartDate(LocalDate.of(2024, 1, 1));
//        timePeriod.setEndDate(LocalDate.of(2024, 1, 5));
//
//        ILocationDTO locationDTO = new LocationDTO();
//        locationDTO.setCity("Paris");
//        locationDTO.setCountryCode("FR");
//
//        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
//
//        when(restTemplate.getForObject(any(), any()))
//                .thenReturn(new TicketMasterEventResponseDTO());
//
//        externalApiService.getTicketmasterEvents(locationDTO, timePeriod);
//
//        verify(restTemplate).getForObject(urlCaptor.capture(), any());
//        assertEquals(expectedUrl, urlCaptor.getValue());
//    }
}
