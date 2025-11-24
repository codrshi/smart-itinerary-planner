package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchNoteType;
import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.PatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryController.class)
public class PatchItineraryControllerTest extends ControllerBaseTest {

    private static final String ITINERARY_ID = "ITIX-FR-NE1QVFROWHKBCFLXWCXNY";
    private static final String URL = URI + "/" + ITINERARY_ID;
    private static final String ERR_MSG_INVALID_PATCH_OPERATION = "Patch operation not found for invalid.";
    private static final String ERR_MSG_MISSING_PATCH_DATA_TARGET = "[patchData.target is null]";
    private static final String ERR_MSG_INVALID_REQUEST_URL = "Invalid request: Request method 'PATCH' is not supported";

    @SneakyThrows
    @Test
    void givenPatchItineraryController_whenCorrectRequest_ThenOkResponse() {

        IPatchItineraryRequestDTO patchItineraryRequestDTO = getJsonObject("ItineraryPlan/patchController_validRequest.json",
                                                                           new TypeReference<PatchItineraryRequestDTO>() {});
        IItineraryResponseDTO patchItineraryResponseDTO = getJsonObject("ItineraryPlan/patchController_validResponse.json",
                                                                      new TypeReference<PatchItineraryResponseDTO>() {});

        when(patchItineraryService.patchItinerary(eq(ITINERARY_ID), any())).thenReturn(patchItineraryResponseDTO);

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .header(Constant.PREFER_HEADER, Constant.PREFER_HEADER_MINIMAL)
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO)))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .header(Constant.PREFER_HEADER, Constant.PREFER_HEADER_REPRESENTATION)
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patchItineraryResponseDTO)));
    }

    @SneakyThrows
    @Test
    void givenPatchItineraryController_whenInvalidPatchOperationRequest_ThenErrorResponse() {

        IPatchItineraryRequestDTO patchItineraryRequestDTO = getJsonObject("ItineraryPlan/patchController_validRequest.json",
                                                                           new TypeReference<PatchItineraryRequestDTO>() {});

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO).replace(
                                        PatchOperation.UPDATE_NOTE.getValue(), "invalid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ERR_MSG_INVALID_PATCH_OPERATION));
    }

    @SneakyThrows
    @Test
    void givenPatchItineraryController_whenInvalidPatchNoteTypeRequest_ThenErrorResponse() {

        IPatchItineraryRequestDTO patchItineraryRequestDTO = getJsonObject("ItineraryPlan/patchController_validRequest.json",
                                                                           new TypeReference<PatchItineraryRequestDTO>() {});

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO).replace(
                                        PatchNoteType.APPEND.getValue(), "invalid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERR_MSG_INVALID_PATCH_DATA_STRUCTURE));
    }

    @SneakyThrows
    @Test
    void givenPatchItineraryController_whenInvalidStructureRequest_ThenErrorResponse() {

        IPatchItineraryRequestDTO patchItineraryRequestDTO = getJsonObject("ItineraryPlan/patchController_invalidStructureRequest.json",
                                                                           new TypeReference<PatchItineraryRequestDTO>() {});

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO).replace(
                                        PatchOperation.UPDATE_NOTE.getValue(), PatchOperation.MOVE_RESOURCE.getValue())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ERR_MSG_MISSING_PATCH_DATA_TARGET));
    }

    @SneakyThrows
    @Test
    void givenPatchItineraryController_whenInvalidUrlRequest_ThenErrorResponse() {

        IPatchItineraryRequestDTO patchItineraryRequestDTO = getJsonObject("ItineraryPlan/patchController_validRequest.json",
                                                                           new TypeReference<PatchItineraryRequestDTO>() {});

        mockMvc.perform(patch(URI).contentType(MediaType.APPLICATION_JSON).with(csrf())
                                .content(objectMapper.writeValueAsString(patchItineraryRequestDTO) ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ERR_MSG_INVALID_REQUEST_URL));
    }
}
