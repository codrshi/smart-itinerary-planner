package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.implementation.async.UpdateResourceEventDTO;

public class UpdateResourceEvent extends ResourceEvent {

    public UpdateResourceEvent(Object source, UpdateResourceEventDTO resourceEventDTO) {
        super(source, resourceEventDTO);
    }
}