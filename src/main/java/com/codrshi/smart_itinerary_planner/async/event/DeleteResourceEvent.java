package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.implementation.async.CreateResourceEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.async.DeleteResourceEventDTO;

public class DeleteResourceEvent extends ResourceEvent {

    public DeleteResourceEvent(Object source, DeleteResourceEventDTO resourceEventDTO) {
        super(source, resourceEventDTO);
    }
}
