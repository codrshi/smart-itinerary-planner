package com.codrshi.smart_itinerary_planner.util.patch.command;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PoiToActivityCommand implements IPatchCommand {
    private ActivityLookup activityLookup;

    public PoiToActivityCommand(ActivityLookup activityLookup) {
        this.activityLookup = activityLookup;
    }

    @Override
    public void execute(String sourceId, String targetId) {
        String sourceActivityId = activityLookup.getActivityId(sourceId);
        String targetActivityId = targetId;

        log.debug("Executing PoiToActivityCommand for sourceId = {} and targetId = {}", sourceId, targetId);

        if(sourceActivityId == null || sourceActivityId.equals(targetActivityId) ||
                !activityLookup.containsActivity(sourceActivityId) || !activityLookup.containsActivity(targetActivityId)){
            log.trace("PoiToActivityCommand execution skipped for sourceId = {} and targetId = {}", sourceId, targetId);
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

        log.trace("updated sourceActivity's POIs = {} and targetActivity's POIs = {}", sourceActivity.getPointOfInterests(), targetActivity.getPointOfInterests());
    }
}
