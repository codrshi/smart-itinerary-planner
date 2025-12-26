package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.implementation.async.CreateResourceEventDTO;

public class CreateResourceEvent extends ResourceEvent {

    public CreateResourceEvent(Object source, CreateResourceEventDTO resourceEventDTO) {
        super(source, resourceEventDTO);
    }
}
