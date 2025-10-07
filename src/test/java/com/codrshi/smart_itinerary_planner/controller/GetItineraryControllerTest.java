package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
                                                                      GetItineraryResponseDTO.class);
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
}
