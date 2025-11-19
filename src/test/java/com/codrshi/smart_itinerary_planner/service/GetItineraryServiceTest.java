package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetItineraryServiceTest extends BaseTest {

    private static final String ITINERARY_ID = "ITIX-US-UCHU9DBX-MWLILSPA74HN";

    @Autowired
    private IGetItineraryService getItineraryService;

    @Test
    public void givenGetItineraryService_whenCorrectRequest_thenSuccess() {
        Itinerary mockedItinerary = getJsonObject("GetItineraryServiceTest/persistedItinerary.json",
                                                            new TypeReference<>() {});
        IItineraryResponseDTO expectedGetItineraryResponseDTO = getJsonObject("GetItineraryServiceTest/GetItineraryValidReponse.json",
                                                            new TypeReference<GetItineraryResponseDTO>() {});

        when(itineraryRepository.findByItineraryId(ITINERARY_ID))
                .thenReturn(Optional.of(mockedItinerary));

        IItineraryResponseDTO getItineraryResponseDTO = getItineraryService.getItinerary(ITINERARY_ID);

        assertEquals(expectedGetItineraryResponseDTO, getItineraryResponseDTO);
    }

    @Test
    void givenGetItineraryService_whenItineraryNotFound_thenThrowResourceNotFoundException() {

        when(itineraryRepository.findByItineraryId(ITINERARY_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> getItineraryService.getItinerary(ITINERARY_ID)
        );

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.formatMessage(Constant.ITINERARY_KEY), ex.getMessage());
    }

    @Test
    void getItineraries_shouldReturnMappedPage_whenValidRequest() {

        IGetItineraryRequestDTO getItineraryRequestDTO = getJsonObject(
                "GetItineraryServiceTest/itineraryFilterRequest.json",
                new TypeReference<>() {});
        List<Itinerary> itineraries = getJsonObject("GetItineraryServiceTest/persistedItineraries.json",
                                                  new TypeReference<>() {});
        Page<Itinerary> itineraryPage = new PageImpl<>(itineraries);


        when(itineraryRepository.searchItineraries(any(), any())).thenReturn(itineraryPage);

        Page<IItineraryResponseDTO> result = getItineraryService.getItineraries(getItineraryRequestDTO, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());

    }


}
