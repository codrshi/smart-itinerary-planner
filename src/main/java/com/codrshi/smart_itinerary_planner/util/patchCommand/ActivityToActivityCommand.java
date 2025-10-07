package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
