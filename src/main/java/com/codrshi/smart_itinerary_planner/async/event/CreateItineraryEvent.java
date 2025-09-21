package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import org.springframework.context.ApplicationEvent;

public class CreateItineraryEvent extends ApplicationEvent {
    private final ICreateItineraryEventDTO createItineraryEventDTO;

    public CreateItineraryEvent(Object source, ICreateItineraryEventDTO createItineraryEventDTO) {
        super(source);
        this.createItineraryEventDTO = createItineraryEventDTO;
    }

    public ICreateItineraryEventDTO getCreateItineraryEventDTO() {
        return createItineraryEventDTO;
    }
}
