package com.codrshi.smart_itinerary_planner.service.implementation.patchHandler;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DeleteResourcePatchHandler extends PatchHandler<DeleteResourcePatchDataDTO> {

    @Override
    protected List<IActivityDTO> applyPatch(List<IActivityDTO> activities, List<DeleteResourcePatchDataDTO> patchDataList) {

        final Set<String> ID_LOOKUP_SET = new HashSet<>(fetchSources(patchDataList));

        return activities.stream()
                .filter(activity ->
                                !ID_LOOKUP_SET.contains(activity.getActivityId()) &&
                                        !ID_LOOKUP_SET.contains(activity.getActivityDate().toString())
                )
                .map(activity -> {
                    List<IPointOfInterestDTO> filteredpoiList = Optional.ofNullable(activity.getPointOfInterests())
                            .map(poiList -> poiList.stream()
                                    .filter(poi -> !ID_LOOKUP_SET.contains(poi.getPoiId()))
                                    .toList())
                            .orElse(null);

                    IActivityDTO copy = new ActivityDTO(activity);
                    copy.setPointOfInterests(filteredpoiList);
                    return copy;
                })
                .toList();
    }

    @Override
    protected List<String> fetchFieldsToValidate(List<DeleteResourcePatchDataDTO> patchDataList) {
        return fetchSources(patchDataList);
    }

    private List<String> fetchSources(List<DeleteResourcePatchDataDTO> patchDataList) {
        return patchDataList.stream().map(DeleteResourcePatchDataDTO::getSource).toList();
    }


}
