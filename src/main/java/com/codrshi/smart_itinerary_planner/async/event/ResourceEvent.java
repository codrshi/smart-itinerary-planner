package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.async.IResourceEventDTO;
import com.codrshi.smart_itinerary_planner.dto.async.ITriggerMailItineraryEventDTO;
import org.springframework.context.ApplicationEvent;

public abstract class ResourceEvent extends ApplicationEvent {
    private IResourceEventDTO resourceEventDTO;

    public ResourceEvent(Object source, IResourceEventDTO resourceEventDTO) {
        super(source);
        this.resourceEventDTO = resourceEventDTO;
    }

    public IResourceEventDTO getResourceEventDTO() {
        return resourceEventDTO;
    }
}
