package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.async.TriggerMailItineraryEventDTO;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(Constant.BASE_URI_ADMIN)
public class AdminController {

    @Autowired
    private TriggerMailItineraryListener triggerMailItineraryListener;

    @PostMapping(Constant.MAIL_ENDPOINT)
    public ResponseEntity<Void> mail(@PathVariable String itineraryId) throws MessagingException {
        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(LocalDate.parse("2025-01-01"));
        timePeriodDTO.setEndDate(LocalDate.parse("2025-01-20"));

        TriggerMailItineraryEventDTO triggerMailItineraryEventDTO = TriggerMailItineraryEventDTO.builder()
                .itineraryId(itineraryId)
                .username("dummy")
                .email("pjdrd24680@gmail.com")
                .destination("city, country")
                .timePeriod(timePeriodDTO)
                .summarizedActivities("This is an AI-generated summary.").build();
        TriggerMailItineraryEvent triggerMailItineraryEvent = new TriggerMailItineraryEvent(this, triggerMailItineraryEventDTO);

        triggerMailItineraryListener.handleTriggerMailItineraryEvent(triggerMailItineraryEvent);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
