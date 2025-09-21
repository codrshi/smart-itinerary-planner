package com.codrshi.smart_itinerary_planner.service.implementation.patchHandler;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.patchCommand.ActivityNode;
import com.codrshi.smart_itinerary_planner.util.patchCommand.PatchCommandDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveResourcePatchHandler extends PatchHandler<MoveResourcePatchDataDTO> {

    @Override
    protected List<IActivityDTO> applyPatch(List<IActivityDTO> activities, List<MoveResourcePatchDataDTO> patchDataList) {
        final Map<String, String> ACTIVITY_DATE_TO_ID_LOOKUP = createActivityDateToIdMap(activities);
        Map<String, IActivityDTO> idToActivityMap = getIdToActivityMap(activities);
        Map<String, ActivityNode> activityAdjacencyMap = getActivityAdjacencyMap(activities);
        Map<String, String> poiToActivityIdMap = getPoIIdToActivityIdMap(activities);
        PatchCommandDispatcher patchCommandDispatcher = new PatchCommandDispatcher(activityAdjacencyMap,
                                                                                   poiToActivityIdMap, idToActivityMap);
        List<IActivityDTO> patchedActivities = new ArrayList<>();

        for (MoveResourcePatchDataDTO patch : patchDataList) {
            String source = patch.getSource();
            String target = patch.getTarget();

            String sourceId = ACTIVITY_DATE_TO_ID_LOOKUP.containsKey(source)?ACTIVITY_DATE_TO_ID_LOOKUP.get(source): source;
            String targetId = ACTIVITY_DATE_TO_ID_LOOKUP.containsKey(target)?ACTIVITY_DATE_TO_ID_LOOKUP.get(target): target;

            patchCommandDispatcher.dispatch(sourceId, targetId);
        }


        idToActivityMap.forEach((id, activity) -> {

            if(activity == null) {
                return;
            }

            List<IPointOfInterestDTO> patchedPoiList = new ArrayList<>();
            for (ActivityNode node = activityAdjacencyMap.get(id); node != null; node = node.getNext()) {
                IActivityDTO nextActivity = idToActivityMap.get(node.getActivityId());
                if (nextActivity != null) {
                    patchedPoiList.addAll(nextActivity.getPointOfInterests());
                }
            }

            if(!patchedPoiList.isEmpty()) {
                IActivityDTO patchedActivity = new ActivityDTO(activity);
                patchedActivity.setPointOfInterests(patchedPoiList);
                patchedActivities.add(patchedActivity);
            }
        });

        return patchedActivities;
    }

    @Override
    protected List<String> fetchFieldsToValidate(List<MoveResourcePatchDataDTO> patchDataList) {
        return patchDataList.stream()
                .flatMap(patchData ->
                                 Stream.of(patchData.getSource(), patchData.getTarget()))
                .toList();
    }

    private Map<String, IActivityDTO> getIdToActivityMap(List<IActivityDTO> activities) {
        return activities.stream().collect(Collectors.toMap(IActivityDTO::getActivityId,activity -> activity));
    }

    private Map<String, ActivityNode> getActivityAdjacencyMap(List<IActivityDTO> activities) {
        return activities.stream().collect(Collectors.toMap(IActivityDTO::getActivityId, activity -> new ActivityNode(activity.getActivityId())));
    }

    private Map<String, String> getPoIIdToActivityIdMap(List<IActivityDTO> activities) {
        Map<String, String> poiToActivityIdMap = new HashMap<>();
        activities.forEach(activity -> activity.getPointOfInterests().forEach(poi -> poiToActivityIdMap.put(poi.getPoiId(), activity.getActivityId())));

        return poiToActivityIdMap;
    }
}
