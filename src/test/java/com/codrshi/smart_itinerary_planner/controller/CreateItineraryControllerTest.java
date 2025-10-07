package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryController.class)
public class CreateItineraryControllerTest extends ControllerBaseTest {

    @SneakyThrows
    @Test
    void givenCreateItineraryController_whenCorrectRequest_ThenAcceptedResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getJsonObject("ItineraryPlan/controller_validRequest.json",
                                                                  CreateItineraryRequestDTO.class);
        ICreateItineraryResponseDTO createItineraryResponseDTO = getJsonObject("ItineraryPlan/controller_validResponse.json",
                                                                    CreateItineraryResponseDTO.class);

        when(createItineraryService.createItinerary(any())).thenReturn(createItineraryResponseDTO);

        mockMvc.perform(post(URI)
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(createItineraryRequestDTO)))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(createItineraryResponseDTO)));
    }

    @SneakyThrows
    @Test
    void givenCreateItineraryController_whenUnknownError_ThenInternalServerErrorResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getJsonObject("ItineraryPlan/controller_validRequest.json",
                                                                  CreateItineraryRequestDTO.class);
        int expectedErrorCode = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        String expectedErrorMessage = ErrorCode.INTERNAL_SERVER_ERROR.getMessageTemplate();

        when(createItineraryService.createItinerary(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post(URI)
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(createItineraryRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
    }

    @SneakyThrows
    @Test
    void givenCreateItineraryController_whenInvalidFields_ThenBadRequestResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getJsonObject("ItineraryPlan/controller_invalidFieldsRequest.json",
                                                                  CreateItineraryRequestDTO.class);

        mockMvc.perform(post(URI)
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(createItineraryRequestDTO)))
                                                 //.replace(DateRangeCriteria.SAME_CITY.getValue(), "INVALID")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                Matchers.containsString("timePeriod.endDate is null")))
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("country is either null or empty")))
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("city contains invalid character(s)")));
    }

    @SneakyThrows
    @Test
    void givenCreateItineraryController_whenInvalidDateRange_ThenBadRequestResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getJsonObject("ItineraryPlan/controller_invalidDateRangeRequest.json",
                                                                  CreateItineraryRequestDTO.class);

        mockMvc.perform(post(URI)
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(createItineraryRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString("[timePeriod.startDate is after timePeriod.endDate]")));
    }

    @SneakyThrows
    @Test
    void givenCreateItineraryController_whenInvalidDate_ThenBadRequestResponse() {
        ICreateItineraryRequestDTO createItineraryRequestDTO = getJsonObject("ItineraryPlan/controller_validRequest.json",
                                                                  CreateItineraryRequestDTO.class);
        final String invalidDate = "2025-%06AND01";

        mockMvc.perform(post(URI)
                                .contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(createItineraryRequestDTO).replace("2025-06-01", invalidDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        Matchers.containsString(String.format("Text '%s' could not be parsed", invalidDate))));
    }
}
