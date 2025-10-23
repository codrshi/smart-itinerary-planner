package com.codrshi.smart_itinerary_planner.dto.async;

public interface IResourceEventDTO {
    String getResourceType();
    void setResourceType(String resourceType);

    String getResourceId();
    void setResourceId(String resourceId);
}
