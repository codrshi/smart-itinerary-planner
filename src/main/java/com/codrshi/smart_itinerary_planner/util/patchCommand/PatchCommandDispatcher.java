package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;

import java.util.List;
import java.util.Map;

public class PatchCommandDispatcher {
    private IPatchCommand poiToActivityCommand;
    private IPatchCommand activityToActivityCommand;

    public PatchCommandDispatcher(ActivityLookup activityLookup) {
        this.poiToActivityCommand = new PoiToActivityCommand(activityLookup);
        this.activityToActivityCommand = new ActivityToActivityCommand(activityLookup);
    }

    public void dispatch(String sourceId, String targetId) {
        if (ActivityUtil.isPoiId(sourceId)) {
            poiToActivityCommand.execute(sourceId, targetId);
        } else {
            activityToActivityCommand.execute(sourceId, targetId);
        }
    }

}

