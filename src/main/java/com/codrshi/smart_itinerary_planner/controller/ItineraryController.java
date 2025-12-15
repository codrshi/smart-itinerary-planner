package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.CreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.DeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.GetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.PatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IDeleteItineraryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.IPatchItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(Constant.BASE_URI)
@Tag(name = "Itinerary management", description = "Endpoints to perform CRUD operations on Itinerary entity.")
public class ItineraryController {

    @Autowired
    private ICreateItineraryService createItineraryService;

    @Autowired
    private IGetItineraryService getItineraryService;

    @Autowired
    private IDeleteItineraryService deleteItineraryService;

    @Autowired
    private IPatchItineraryService patchItineraryService;

    @Autowired
    private PagedResourcesAssembler<IItineraryResponseDTO> pagedResourcesAssembler;

    // TODO: i18n support
    // TODO: Scheduler task to purge itineraries after a certain period from creation.
    // TODO: date range limit of 1 month
    // TODO: admin controller endpoint to invalidate cache
    @Operation(summary = "Create an itinerary",
               description = "Create an itinerary in a given city and country for a given date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created itinerary",
                         content = @Content(schema = @Schema(implementation = CreateItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Activities construction failed",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "502", description = "Invalid External API response",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @PostMapping
    public ResponseEntity<EntityModel<ICreateItineraryResponseDTO>> createItinerary(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create itinerary request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateItineraryRequestDTO.class))
            )
            @Valid
            @RequestBody
            ICreateItineraryRequestDTO createItineraryEventDTO) {
        ICreateItineraryResponseDTO createItineraryResponseDTO = createItineraryService.createItinerary(createItineraryEventDTO);

        EntityModel<ICreateItineraryResponseDTO> responseModel = EntityModel.of(createItineraryResponseDTO,
                                                                          linkTo(methodOn(ItineraryController.class).getItinerary(createItineraryResponseDTO.getItineraryId())).withRel("print itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Operation(summary = "Get the itinerary by ID",
               description = "Fetches the itinerary details for a given itinerary ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched itinerary",
                         content = @Content(schema = @Schema(implementation = GetItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @GetMapping(Constant.GET_ENDPOINT)
    public ResponseEntity<EntityModel<IItineraryResponseDTO>> getItinerary(
            @Parameter(description = "Itinerary ID", required = true, example = "ITIX-US-TIVVDB3-EINT3H8HF73JV")
            @PathVariable
            String itineraryId) {
        IItineraryResponseDTO itineraryResponseDTO = getItineraryService.getItinerary(itineraryId);

        EntityModel<IItineraryResponseDTO> responseModel = EntityModel.of(itineraryResponseDTO,
                                                                                linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"),
                                                                                linkTo(methodOn(ItineraryController.class).patchItinerary(itineraryId, null,null)).withRel("update itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).deleteItinerary(itineraryId)).withRel("delete itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Operation(summary = "Get itineraries by filter",
               description = "Fetches multiple itinerary details in a paginated manner based on the filter on city, country and date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched itinerary",
                         content = @Content(schema = @Schema(implementation = GetItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<IItineraryResponseDTO>>> getItineraries(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Get itinerary request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GetItineraryRequestDTO.class))
            )
            @Valid
            @ModelAttribute
            GetItineraryRequestDTO getItineraryRequestDTO,
            Pageable pageable) {
        Page<IItineraryResponseDTO> itineraries = getItineraryService.getItineraries(getItineraryRequestDTO,
                                                                                     pageable);

        PagedModel<EntityModel<IItineraryResponseDTO>> responseModel = pagedResourcesAssembler.toModel(itineraries, itinerary ->
                EntityModel.of(itinerary,
                               linkTo(methodOn(ItineraryController.class).patchItinerary(itinerary.getItineraryId(),
                                                                                         null,null)).withRel("modify itinerary"),
                               linkTo(methodOn(ItineraryController.class).deleteItinerary(itinerary.getItineraryId())).withRel("delete itinerary")
                ));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Operation(summary = "Delete the itinerary by ID",
               description = "Deletes the itinerary for a given itinerary ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted itinerary",
                         content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @DeleteMapping(Constant.DELETE_ENDPOINT)
    public ResponseEntity<Void> deleteItinerary(
            @Parameter(description = "Itinerary ID", required = true, example = "ITIX-US-TIVVDB3-EINT3H8HF73JV")
            @PathVariable
            String itineraryId) {
        deleteItineraryService.deleteItinerary(itineraryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Delete itineraries by filter",
               description = "Delete multiple itineraries based on the filter on city, country and date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deletes itineraries",
                         content = @Content(schema = @Schema(implementation = DeleteItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @DeleteMapping
    public ResponseEntity<EntityModel<IDeleteItineraryResponseDTO>> deleteItineraries(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Delete itinerary request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeleteItineraryRequestDTO.class))
            )
            @Valid
            DeleteItineraryRequestDTO deleteItineraryRequestDTO) {
        IDeleteItineraryResponseDTO deleteItineraryResponseDTO =
                deleteItineraryService.deleteItineraries(deleteItineraryRequestDTO);

        EntityModel<IDeleteItineraryResponseDTO> responseModel = EntityModel.of(deleteItineraryResponseDTO,
                                                                          linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PatchMapping(Constant.PATCH_ENDPOINT)
    @Operation(summary = "Patches an itinerary",
               description = """
                    Updates the itinerary details for a given itinerary ID. Patching includes updating notes, restructuring POIs and/or activities.
                    
                    The Prefer header is used to indicate the representation of the response:
                    - `Prefer: return-representation`: Returns updated response (200 OK).
                    - `Prefer: return-minimal` or header absent: No response body (204 NO CONTENT).
                    """)
    @Parameter(name = "Prefer", in = ParameterIn.HEADER, description = "Controls response verbosity", example = "return-representation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully patched itinerary",
                         content = @Content(schema = @Schema(implementation = PatchItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "Successfully patched itinerary",
                         content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    public ResponseEntity<EntityModel<IItineraryResponseDTO>> patchItinerary(
            @Parameter(description = "Itinerary ID", required = true, example = "ITIX-US-TIVVDB3-EINT3H8HF73JV")
            @PathVariable
            String itineraryId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Patch itinerary request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PatchItineraryRequestDTO.class))
            )
            @Valid
            @RequestBody
            PatchItineraryRequestDTO patchItineraryRequestDTO,
            @RequestHeader
            HttpHeaders httpHeaders) {

        IItineraryResponseDTO iItineraryResponseDTO = patchItineraryService.patchItinerary(itineraryId, patchItineraryRequestDTO);

        ResponseEntity<EntityModel<IItineraryResponseDTO>> responseEntity;

        if(Constant.PREFER_HEADER_REPRESENTATION.equalsIgnoreCase(httpHeaders.getFirst(Constant.PREFER_HEADER))) {
            EntityModel<IItineraryResponseDTO> responseModel = EntityModel.of(iItineraryResponseDTO,
                                                                              linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"),
                                                                              linkTo(methodOn(ItineraryController.class).getItinerary(itineraryId)).withRel("print itinerary"),
                                                                              linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"),
                                                                              linkTo(methodOn(ItineraryController.class).deleteItinerary(itineraryId)).withRel("delete itinerary"));

            responseEntity = ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(responseModel);
        } else {  // return-minimal
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return responseEntity;
    }
}
