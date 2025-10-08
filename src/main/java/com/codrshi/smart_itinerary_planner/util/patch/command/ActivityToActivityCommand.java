package com.codrshi.smart_itinerary_planner.util.patch.command;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;

import java.util.ArrayList;
import java.util.List;

public class ActivityToActivityCommand implements IPatchCommand {
    private ActivityLookup activityLookup;

    public ActivityToActivityCommand(ActivityLookup activityLookup) {
        this.activityLookup = activityLookup;
    }

    @Override
    public void execute(String sourceId, String targetId) {

        if(sourceId.equals(targetId) || !activityLookup.containsActivity(sourceId) || !activityLookup.containsActivity(targetId)){
            return;
        }

        IActivityDTO sourceActivity = activityLookup.fromActivityId(sourceId);
        IActivityDTO targetActivity = activityLookup.fromActivityId(targetId);

        List<IPointOfInterestDTO> poiList = sourceActivity.getPointOfInterests();

        targetActivity.getPointOfInterests().addAll(poiList);
        sourceActivity.setPointOfInterests(new ArrayList<>());
    }
}
