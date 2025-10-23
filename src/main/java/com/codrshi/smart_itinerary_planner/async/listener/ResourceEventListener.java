package com.codrshi.smart_itinerary_planner.async.listener;

import com.codrshi.smart_itinerary_planner.async.event.ResourceEvent;
import com.codrshi.smart_itinerary_planner.dto.async.IResourceEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResourceEventListener {

    @EventListener
    @Async
    public void handleResourceEvent(ResourceEvent event) {

        if(event == null || event.getResourceEventDTO() == null) {
            log.warn("Received null event or event data");
        }

        String resourceEventType = event.getClass().getSimpleName();

        IResourceEventDTO resourceEventDTO = event.getResourceEventDTO();
        log.debug("Received {}: {}",resourceEventType, resourceEventDTO);
    }
}
