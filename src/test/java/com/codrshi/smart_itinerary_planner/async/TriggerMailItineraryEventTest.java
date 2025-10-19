package com.codrshi.smart_itinerary_planner.async;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.async.event.TriggerMailItineraryEvent;
import com.codrshi.smart_itinerary_planner.async.listener.TriggerMailItineraryListener;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TriggerMailItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.service.IAiModelService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

public class TriggerMailItineraryEventTest extends BaseTest {

    @Autowired
    private TriggerMailItineraryListener triggerMailItineraryEventListener;

//    @MockitoBean
//    private IAiModelService aiModelService;
//
//    @MockitoBean
//    private ChatClient chatClient;

    @Test
    public void testTriggerMailItineraryEvent() {
        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(LocalDate.parse("2025-01-01"));
        timePeriodDTO.setEndDate(LocalDate.parse("2025-01-20"));

        TriggerMailItineraryEventDTO triggerMailItineraryEventDTO = TriggerMailItineraryEventDTO.builder()
                 .itineraryId("ITIX-IN-ABC")
                .username("dummy")
                .email("pjdrd24680@gmail.com")
                .destination("city, country")
                .timePeriod(timePeriodDTO)
                .summarizedActivities("This is an AI-generated summary.").build();
        TriggerMailItineraryEvent triggerMailItineraryEvent = new TriggerMailItineraryEvent(this,triggerMailItineraryEventDTO);

        triggerMailItineraryEventListener.handleTriggerMailItineraryEvent(triggerMailItineraryEvent);

    }
}
