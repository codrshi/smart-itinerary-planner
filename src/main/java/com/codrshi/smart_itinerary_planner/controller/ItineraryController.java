package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.GetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.PatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IDeleteItineraryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.IPatchItineraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // TODO: add HATEOS links
    // TODO: internationalization support
    @PostMapping
    public ResponseEntity<ICreateItineraryResponseDTO> createItinerary(@Valid @RequestBody ICreateItineraryRequestDTO createItineraryEventDTO) {
        ICreateItineraryResponseDTO createItineraryResponseDTO = createItineraryService.createItinerary(createItineraryEventDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createItineraryResponseDTO);
    }

    @GetMapping(Constant.GET_ENDPOINT)
    public ResponseEntity<IItineraryResponseDTO> getItinerary(@PathVariable String itineraryId) {
        IItineraryResponseDTO itineraryResponseDTO = getItineraryService.getItinerary(itineraryId);
        return ResponseEntity.status(HttpStatus.OK).body(itineraryResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<IItineraryResponseDTO>> getItineraries(@Valid GetItineraryRequestDTO getItineraryRequestDTO,
                                                                      Pageable pageable) {
        System.out.println(getItineraryRequestDTO);
        Page<IItineraryResponseDTO> itineraries = getItineraryService.getItineraries(getItineraryRequestDTO, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(itineraries);
    }

    @DeleteMapping(Constant.DELETE_ENDPOINT)
    public ResponseEntity<Void> deleteItinerary(@PathVariable String itineraryId) {
        deleteItineraryService.deleteItinerary(itineraryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<IDeleteItineraryResponseDTO> deleteItineraries(@Valid DeleteItineraryRequestDTO deleteItineraryRequestDTO ) {
        IDeleteItineraryResponseDTO deleteItineraryResponseDTO = deleteItineraryService.deleteItineraries(deleteItineraryRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(deleteItineraryResponseDTO);
    }

    @PatchMapping(Constant.PATCH_ENDPOINT)
    public ResponseEntity<IItineraryResponseDTO> patchItinerary(@PathVariable String itineraryId,
                                               @Valid @RequestBody PatchItineraryRequestDTO patchItineraryRequestDTO,
                                               @RequestHeader HttpHeaders httpHeaders) {

        IItineraryResponseDTO iItineraryResponseDTO = patchItineraryService.patchItinerary(itineraryId, patchItineraryRequestDTO);

        ResponseEntity<IItineraryResponseDTO> responseEntity;

        if(Constant.PREFER_HEADER_REPRESENTATION.equalsIgnoreCase(httpHeaders.getFirst(Constant.PREFER_HEADER))) {
            responseEntity = ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(iItineraryResponseDTO);
        } else {
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return responseEntity;
    }
}
