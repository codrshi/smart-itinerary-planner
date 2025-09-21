package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.controller.ItineraryController;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.LocationDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TicketMasterEventResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ExternalApiServiceTest extends BaseTest{

    @Autowired
    private ExternalApiService externalApiService;

    @Test
    void givenExternalApiService_whenGetTicketmasterEvents_thenCorrectUrl() {

        String expectedUrl = "https://app.ticketmaster.com/discovery/v2/events.json?" +
                "apikey=dummyKey&city=Paris&country=FR&" +
                "startDateTime=2024-01-01T00:00:00Z&endDateTime=2024-01-05T23:59:59Z";

        ITimePeriodDTO timePeriod = new TimePeriodDTO();
        timePeriod.setStartDate(LocalDate.of(2024, 1, 1));
        timePeriod.setEndDate(LocalDate.of(2024, 1, 5));

        ILocationDTO locationDTO = new LocationDTO();
        locationDTO.setCity("Paris");
        locationDTO.setCountryCode("FR");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        when(restTemplate.getForObject(any(), any()))
                .thenReturn(new TicketMasterEventResponseDTO());

        externalApiService.getTicketmasterEvents(locationDTO, timePeriod);

        verify(restTemplate).getForObject(urlCaptor.capture(), any());
        assertEquals(expectedUrl, urlCaptor.getValue());
    }
}
