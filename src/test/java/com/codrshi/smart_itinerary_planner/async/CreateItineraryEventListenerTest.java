package com.codrshi.smart_itinerary_planner.async;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.async.event.CreateItineraryEvent;
import com.codrshi.smart_itinerary_planner.async.listener.CreateItineraryEventListener;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryEventDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateItineraryEventListenerTest extends BaseTest {

    @Autowired
    private CreateItineraryEventListener createItineraryEventListener;

    @Test
    public void testHandleCreateItineraryEvent() {
        ICreateItineraryEventDTO createItineraryEventDTO = getJsonObject("async/async_validCreateItineraryEvent.json",
                                                                         new TypeReference<CreateItineraryEventDTO>() {});

        createItineraryEventListener.handleCreateItineraryEvent(new CreateItineraryEvent("test",
                                                                                         createItineraryEventDTO));
    }
}
