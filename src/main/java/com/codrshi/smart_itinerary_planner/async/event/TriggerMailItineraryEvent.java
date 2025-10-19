package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ITriggerMailItineraryEventDTO;
import org.springframework.context.ApplicationEvent;

public class TriggerMailItineraryEvent extends ApplicationEvent {
    private final ITriggerMailItineraryEventDTO triggerMailItineraryEventDTO;

    public TriggerMailItineraryEvent(Object source, ITriggerMailItineraryEventDTO triggerMailItineraryEventDTO) {
        super(source);
        this.triggerMailItineraryEventDTO = triggerMailItineraryEventDTO;
    }

    public ITriggerMailItineraryEventDTO getTriggerMailItineraryEventDTO() {
        return triggerMailItineraryEventDTO;
    }

}
