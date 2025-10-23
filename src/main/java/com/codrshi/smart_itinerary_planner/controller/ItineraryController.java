package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
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
    // TODO: Rate limiting using redis
    // TODO: store invalid mail to redis with long TTL
    // TODO: date range limit of 1 month
    // TODO: admin controller endpoint to invalidate cache
    @PostMapping
    public ResponseEntity<EntityModel<ICreateItineraryResponseDTO>> createItinerary(@Valid @RequestBody ICreateItineraryRequestDTO createItineraryEventDTO) {
        ICreateItineraryResponseDTO createItineraryResponseDTO = createItineraryService.createItinerary(createItineraryEventDTO);

        EntityModel<ICreateItineraryResponseDTO> responseModel = EntityModel.of(createItineraryResponseDTO,
                                                                          linkTo(methodOn(ItineraryController.class).getItinerary(createItineraryResponseDTO.getItineraryId())).withRel("print itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping(Constant.GET_ENDPOINT)
    public ResponseEntity<EntityModel<IItineraryResponseDTO>> getItinerary(@PathVariable String itineraryId) {
        IItineraryResponseDTO itineraryResponseDTO = getItineraryService.getItinerary(itineraryId);

        EntityModel<IItineraryResponseDTO> responseModel = EntityModel.of(itineraryResponseDTO,
                                                                                linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"),
                                                                                linkTo(methodOn(ItineraryController.class).patchItinerary(itineraryId, null,null)).withRel("update itinerary"),
                                                                                linkTo(methodOn(ItineraryController.class).deleteItinerary(itineraryId)).withRel("delete itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<IItineraryResponseDTO>>> getItineraries(@Valid GetItineraryRequestDTO getItineraryRequestDTO,
                                                                                         Pageable pageable) {
        System.out.println(getItineraryRequestDTO);
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

    @DeleteMapping(Constant.DELETE_ENDPOINT)
    public ResponseEntity<Void> deleteItinerary(@PathVariable String itineraryId) {
        deleteItineraryService.deleteItinerary(itineraryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<EntityModel<IDeleteItineraryResponseDTO>> deleteItineraries(@Valid DeleteItineraryRequestDTO deleteItineraryRequestDTO ) {
        IDeleteItineraryResponseDTO deleteItineraryResponseDTO =
                deleteItineraryService.deleteItineraries(deleteItineraryRequestDTO);

        EntityModel<IDeleteItineraryResponseDTO> responseModel = EntityModel.of(deleteItineraryResponseDTO,
                                                                          linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PatchMapping(Constant.PATCH_ENDPOINT)
    public ResponseEntity<EntityModel<IItineraryResponseDTO>> patchItinerary(@PathVariable String itineraryId,
                                               @Valid @RequestBody PatchItineraryRequestDTO patchItineraryRequestDTO,
                                               @RequestHeader HttpHeaders httpHeaders) {

        IItineraryResponseDTO iItineraryResponseDTO = patchItineraryService.patchItinerary(itineraryId, patchItineraryRequestDTO);

        ResponseEntity<EntityModel<IItineraryResponseDTO>> responseEntity;

        if(Constant.PREFER_HEADER_REPRESENTATION.equalsIgnoreCase(httpHeaders.getFirst(Constant.PREFER_HEADER))) {
            EntityModel<IItineraryResponseDTO> responseModel = EntityModel.of(iItineraryResponseDTO,
                                                                              linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("create itinerary"),
                                                                              linkTo(methodOn(ItineraryController.class).getItinerary(itineraryId)).withRel("print itinerary"),
                                                                              linkTo(methodOn(ItineraryController.class).getItineraries(null,null)).withRel("print itineraries"),
                                                                              linkTo(methodOn(ItineraryController.class).deleteItinerary(itineraryId)).withRel("delete itinerary"));

            responseEntity = ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(responseModel);
        } else {
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return responseEntity;
    }
}
