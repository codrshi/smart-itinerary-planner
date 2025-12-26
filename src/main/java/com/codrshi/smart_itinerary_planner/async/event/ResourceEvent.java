package com.codrshi.smart_itinerary_planner.async.event;

import com.codrshi.smart_itinerary_planner.dto.async.IResourceEventDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class ResourceEvent extends ApplicationEvent {
    private IResourceEventDTO resourceEventDTO;

    public ResourceEvent(Object source, IResourceEventDTO resourceEventDTO) {
        super(source);
        this.resourceEventDTO = resourceEventDTO;
    }
}
