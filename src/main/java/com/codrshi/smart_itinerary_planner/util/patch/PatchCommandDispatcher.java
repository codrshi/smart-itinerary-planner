package com.codrshi.smart_itinerary_planner.util.patch;

import com.codrshi.smart_itinerary_planner.util.ActivityLookup;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.patch.command.ActivityToActivityCommand;
import com.codrshi.smart_itinerary_planner.util.patch.command.IPatchCommand;
import com.codrshi.smart_itinerary_planner.util.patch.command.PoiToActivityCommand;

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

