package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.TestConfig;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.GetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryController.class)
public class GetItineraryControllerTest extends ControllerBaseTest{

    public static final String ITINERARY_ID = "ITIX-FR-NE1QVFROWHKBCFLXWCXNY";
    private static final String URL = URI + "/" + ITINERARY_ID;

    @SneakyThrows
    @Test
    void givenGetItineraryController_whenCorrectRequest_ThenOkResponse() {

        IItineraryResponseDTO getItineraryResponseDTO = getJsonObject("ItineraryPlan/controller_validGetResponse.json",
                                                                      new TypeReference<GetItineraryResponseDTO>() {});
        when(getItineraryService.getItinerary(any())).thenReturn(getItineraryResponseDTO);

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getItineraryResponseDTO)));
    }

    @SneakyThrows
    @Test
    void givenGetItineraryController_whenItineraryMissing_Then404Response() {

        int expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND.getCode();
        String expectedErrorMessage = String.format(ErrorCode.RESOURCE_NOT_FOUND.getMessageTemplate(), "itinerary");

        when(getItineraryService.getItinerary(any())).thenThrow(new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY));

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
    }

    @SneakyThrows
    @Test
    void givenGetItinerariesController_whenCorrectRequest_ThenOkResponse() {
        TestConfig.PageWrapper<IItineraryResponseDTO, GetItineraryResponseDTO> getItineraryResponsePageWrapper =
                getJsonObject("ItineraryPlan/getController_ValidFilterResponse.json",
                              new TypeReference<>() {});

        when(getItineraryService.getItineraries(any(), any())).thenReturn(getItineraryResponsePageWrapper.toPage());

        mockMvc.perform(get(URI)
                                .param("city", "Paris")
                                .param("country", "France")
                                .param("startDate", "2025-06-01")
                                .param("endDate", "2025-06-10")
                                .param("dateRangeCriteria", "sameDateRange")
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.getItineraryResponseDTOList[0].itineraryId").value(getItineraryResponsePageWrapper.getContent().get(0).getItineraryId()))
                .andExpect(jsonPath("$._embedded.getItineraryResponseDTOList[1].itineraryId").value(getItineraryResponsePageWrapper.getContent().get(1).getItineraryId()))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.size").value(10));

    }

    @SneakyThrows
    @Test
    void givenGetItinerariesController_whenIncorrectRequest_ThenErrorResponse() {
        TestConfig.PageWrapper<IItineraryResponseDTO, GetItineraryResponseDTO> getItineraryResponsePageWrapper =
                getJsonObject("ItineraryPlan/getController_ValidFilterResponse.json",
                              new TypeReference<>() {});

        when(getItineraryService.getItineraries(any(), any())).thenReturn(getItineraryResponsePageWrapper.toPage());

        mockMvc.perform(get(URI)
                                .param("city", "Pari4s")
                                .param("country", "Fr#ance")
                                .param("startDate", "2025-06-01")
                                .param("endDate", "2025-06-10")
                                .param("dateRangeCriteria", "same_Date_Range")
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("Failed to convert property value of type 'java.lang.String' to required type 'com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria' for property 'dateRangeCriteria'")))
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("city contains invalid character(s)")))
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("country contains invalid character(s)")));

    }
}
