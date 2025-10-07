package com.codrshi.smart_itinerary_planner.service.implementation.patchHandler;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import com.codrshi.smart_itinerary_planner.util.ActivityLookup;
import com.codrshi.smart_itinerary_planner.util.patchCommand.PatchCommandDispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveResourcePatchHandler extends PatchHandler<MoveResourcePatchDataDTO> {

    @Override
    protected List<IActivityDTO> applyPatch(List<IActivityDTO> activities, List<MoveResourcePatchDataDTO> patchDataList) {
        final Map<String, String> ACTIVITY_DATE_TO_ID_LOOKUP = createActivityDateToIdMap(activities);

        ActivityLookup activityLookup = new ActivityLookup(activities);
        PatchCommandDispatcher patchCommandDispatcher = new PatchCommandDispatcher(activityLookup);

        for (MoveResourcePatchDataDTO patch : patchDataList) {
            String source = patch.getSource();
            String target = patch.getTarget();

            String sourceId = ACTIVITY_DATE_TO_ID_LOOKUP.containsKey(source)?ACTIVITY_DATE_TO_ID_LOOKUP.get(source): source;
            String targetId = ACTIVITY_DATE_TO_ID_LOOKUP.containsKey(target)?ACTIVITY_DATE_TO_ID_LOOKUP.get(target): target;

            patchCommandDispatcher.dispatch(sourceId, targetId);
        }

        return activityLookup.getFilteredActivities();
    }

    @Override
    protected List<String> fetchFieldsToValidate(List<MoveResourcePatchDataDTO> patchDataList) {
        return patchDataList.stream()
                .flatMap(patchData ->
                                 Stream.of(patchData.getSource(), patchData.getTarget()))
                .toList();
    }
}
