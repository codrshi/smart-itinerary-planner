package com.codrshi.smart_itinerary_planner.dto.implementation.async;

import com.codrshi.smart_itinerary_planner.dto.async.IResourceEventDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteResourceEventDTO implements IResourceEventDTO {
    private String resourceType;
    private String resourceId;
}
