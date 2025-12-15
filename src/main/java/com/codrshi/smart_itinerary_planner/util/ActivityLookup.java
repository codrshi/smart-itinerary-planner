package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityLookup {
    private Map<String, String> poiToActivityIdMap;
    private Map<String, IActivityDTO> idToActivityMap;

    public ActivityLookup(List<IActivityDTO> activities) {
        idToActivityMap = activities.stream().collect(Collectors.toMap(IActivityDTO::getActivityId, activity -> activity));

        poiToActivityIdMap = new HashMap<>();
        activities.forEach(activity -> activity.getPointOfInterests().forEach(poi -> poiToActivityIdMap.put(poi.getPoiId(), activity.getActivityId())));

    }

    public boolean containsActivity(String activityId) {
        return idToActivityMap.containsKey(activityId);
    }

    public IActivityDTO fromActivityId(String activityId) {
        return idToActivityMap.get(activityId);
    }

    public String getActivityId(String poiId) {
        return poiToActivityIdMap.get(poiId);
    }

    public List<IActivityDTO> getFilteredActivities() {
        return idToActivityMap.values().stream().filter(activity -> activity.getPointOfInterests()!=null &&
                !activity.getPointOfInterests().isEmpty()).collect(Collectors.toList());
    }
}
