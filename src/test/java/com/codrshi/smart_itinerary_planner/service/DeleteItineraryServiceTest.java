package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.request.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteItineraryServiceTest extends BaseTest {

    private static final String ITINERARY_1_ID = "ITIX-US-UCHU9DBX-MWLILSPA74HN";
    private static final String ITINERARY_2_ID = "ITIX-US-TIVVDB3-EINT3H8HF73JV";

    @Autowired
    private IDeleteItineraryService deleteItineraryService;

    @Test
    void givenDeleteItineraryService_whenCorrectRequest_thenDeleteItinerary() {
        when(itineraryRepository.deleteByItineraryIdAndUserRef(ITINERARY_1_ID, RequestContext.getUsername())).thenReturn(1);

        assertDoesNotThrow(() -> deleteItineraryService.deleteItinerary(ITINERARY_1_ID));

        verify(itineraryRepository).deleteByItineraryIdAndUserRef(ITINERARY_1_ID, RequestContext.getUsername());
    }

    @Test
    void givenDeleteItineraryService_whenItineraryDoesNotExist_ThrowResourceNotFoundException() {

        when(itineraryRepository.deleteByItineraryIdAndUserRef(ITINERARY_1_ID, RequestContext.getUsername())).thenReturn(0);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteItineraryService.deleteItinerary(ITINERARY_1_ID)
        );

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.formatMessage(Constant.ITINERARY_KEY), ex.getMessage());
    }

    @Test
    void givenDeleteItineraryService_whenDeleteItineraries_thenDeleteItineraries() {
        IDeleteItineraryRequestDTO deleteItineraryRequestDTO = getJsonObject(
                "GetItineraryServiceTest/itineraryFilterRequest.json",
                new TypeReference<>() {});

        Itinerary itinerary1 = Itinerary.builder().itineraryId(ITINERARY_1_ID).build();
        Itinerary itinerary2 = Itinerary.builder().itineraryId(ITINERARY_2_ID).build();

        when(itineraryRepository.deleteItineraries(any())).thenReturn(List.of(itinerary1, itinerary2));


        IDeleteItineraryResponseDTO itineraryResponseDTO =
                deleteItineraryService.deleteItineraries(deleteItineraryRequestDTO);

        assertEquals(List.of(ITINERARY_1_ID, ITINERARY_2_ID), itineraryResponseDTO.getItineraryIds());
        assertEquals(2, itineraryResponseDTO.getCount());
    }

}
