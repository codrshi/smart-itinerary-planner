package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.AuxiliaryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import com.codrshi.smart_itinerary_planner.service.IAuxiliaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(Constant.BASE_URI_ASSISTANT)
@Tag(name = "AI Assistant", description = "Endpoints to interact with AI assistant.")
public class AuxiliaryController {

    @Autowired
    private IAuxiliaryService auxiliaryService;

    @Operation(summary = "Mail an itinerary by ID",
               description = "Sends itinerary details for a given itinerary ID to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully triggered event to mail itinerary",
                         content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @PostMapping(Constant.MAIL_ENDPOINT)
    public ResponseEntity<Void> mail(
            @Parameter(description = "Itinerary ID", required = true, example = "ITIX-US-TIVVDB3-EINT3H8HF73JV")
            @PathVariable
            String itineraryId) throws IOException, MessagingException {
        auxiliaryService.mailItinerary(itineraryId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Query about itinerary",
               description = "Sends the user query regarding itinerary as a prompt to the AI assistant and returns the response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated assistant response",
                         content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @PostMapping
    public ResponseEntity<Object> query(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuxiliaryRequestDTO.class))
            )
            @Valid
            @RequestBody IAuxiliaryRequestDTO auxiliaryRequestDTO) {

        Object response = auxiliaryService.query(auxiliaryRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
