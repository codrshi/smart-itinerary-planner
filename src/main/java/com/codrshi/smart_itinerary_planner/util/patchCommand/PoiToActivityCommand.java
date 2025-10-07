package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;

import java.util.Map;
import java.util.Optional;

public class PoiToActivityCommand implements IPatchCommand {
    private ActivityLookup activityLookup;

    public PoiToActivityCommand(ActivityLookup activityLookup) {
        this.activityLookup = activityLookup;
    }

    @Override
    public void execute(String sourceId, String targetId) {
        String sourceActivityId = activityLookup.getActivityId(sourceId);
        String targetActivityId = targetId;

        if(sourceActivityId == null ||
                sourceActivityId.equals(targetActivityId) || !activityLookup.containsActivity(sourceActivityId) || !activityLookup.containsActivity(targetActivityId)){
            return;
        }

        IActivityDTO sourceActivity = activityLookup.fromActivityId(sourceActivityId);
        IActivityDTO targetActivity = activityLookup.fromActivityId(targetActivityId);

        Optional<IPointOfInterestDTO> patchedPoi =
                sourceActivity.getPointOfInterests().stream().filter(poi -> poi.getPoiId().equals(sourceId)).findFirst();

        patchedPoi.ifPresent(poi -> {
            sourceActivity.getPointOfInterests().remove(poi);
            targetActivity.getPointOfInterests().add(poi);
        });
    }
}
