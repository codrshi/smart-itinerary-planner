package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import com.codrshi.smart_itinerary_planner.service.IAuxiliaryService;
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
public class AuxiliaryController {

    @Autowired
    private IAuxiliaryService auxiliaryService;

    @PostMapping(Constant.MAIL_ENDPOINT)
    public ResponseEntity<Void> mail(@PathVariable String itineraryId) throws IOException, MessagingException {
        auxiliaryService.mailItinerary(itineraryId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping
    public ResponseEntity<Object> query(@Valid @RequestBody IAuxiliaryRequestDTO auxiliaryRequestDTO) {

        Object response = auxiliaryService.query(auxiliaryRequestDTO);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
