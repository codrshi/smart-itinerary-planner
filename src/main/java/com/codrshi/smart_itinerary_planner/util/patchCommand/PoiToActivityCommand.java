package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;

import java.util.Map;
import java.util.Optional;

public class PoiToActivityCommand implements IPatchCommand {
    private Map<String, String> poiToActivityIdMap;
    private Map<String, IActivityDTO> idToActivityMap;

    public PoiToActivityCommand(Map<String, String> poiToActivityIdMap, Map<String, IActivityDTO> idToActivityMap) {
        this.poiToActivityIdMap = poiToActivityIdMap;
        this.idToActivityMap = idToActivityMap;
    }

    @Override
    public void execute(String sourceId, String targetId) {
        String sourceActivityId = poiToActivityIdMap.get(sourceId);
        if(sourceActivityId == null) {
            return;
        }

        IActivityDTO sourceActivity = idToActivityMap.get(sourceActivityId);
        IActivityDTO targetActivity = idToActivityMap.get(targetId);

        if(sourceActivity == null || targetActivity == null) {
            return;
        }

        Optional<IPointOfInterestDTO> patchedPoi =
                sourceActivity.getPointOfInterests().stream().filter(poi -> poi.getPoiId().equals(sourceId)).findFirst();

        patchedPoi.ifPresent(poi -> {
            sourceActivity.getPointOfInterests().remove(poi);
            targetActivity.getPointOfInterests().add(poi);
        });
    }
}
