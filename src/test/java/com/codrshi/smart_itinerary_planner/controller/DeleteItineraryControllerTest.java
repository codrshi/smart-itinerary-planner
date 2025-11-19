package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.TestConfig;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.service.IDeleteItineraryService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryController.class)
public class DeleteItineraryControllerTest extends ControllerBaseTest{

    private static final String ITINERARY_ID = "ITIX-FR-NE1QVFROWHKBCFLXWCXNY";
    private static final String URL = URI + "/" + ITINERARY_ID;

    @SneakyThrows
    @Test
    void givenDeleteItineraryController_whenCorrectRequest_ThenOkResponse() {

        doNothing().when(deleteItineraryService).deleteItinerary(anyString());

        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isNoContent());
    }

    @SneakyThrows
    @Test
    void givenDeleteItineraryController_whenItineraryMissing_Then404Response() {

        int expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND.getCode();
        String expectedErrorMessage = String.format(ErrorCode.RESOURCE_NOT_FOUND.getMessageTemplate(), "itinerary");

        doThrow(new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_ITINERARY)).when(deleteItineraryService).deleteItinerary(anyString());


        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
    }

    @SneakyThrows
    @Test
    void givenGetItinerariesController_whenCorrectRequest_ThenOkResponse() {
        IDeleteItineraryResponseDTO deleteItineraryResponseDTO =
                getJsonObject("ItineraryPlan/deleteController_ValidResponse.json",
                              new TypeReference<DeleteItineraryResponseDTO>() {});

        when(deleteItineraryService.deleteItineraries(any())).thenReturn(deleteItineraryResponseDTO);

        mockMvc.perform(delete(URI)
                                .param("city", "Paris")
                                .param("country", "France")
                                .param("startDate", "2025-06-01")
                                .param("endDate", "2025-06-10")
                                .param("dateRangeCriteria", "sameDateRange")
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(deleteItineraryResponseDTO)));
    }
}
