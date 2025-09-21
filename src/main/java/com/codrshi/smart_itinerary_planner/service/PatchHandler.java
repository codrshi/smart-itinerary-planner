package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PatchHandler<T extends IPatchDataDTO> {

    @Autowired
    private IValidationService validationService;

    public final List<IActivityDTO> handle(List<IActivityDTO> activities, List<T> patchDataList) {

        if(patchDataList.isEmpty() || activities == null || activities.isEmpty()) {
            return activities;
        }

        List<String> fields = fetchFieldsToValidate(patchDataList);
        validationService.validatePatchData(fields);

        List<IActivityDTO> patchedActivities = applyPatch(activities, patchDataList);
        patchedActivities.sort(Comparator.comparing(IActivityDTO::getActivityDate));

        return patchedActivities;
    }

    protected Map<String, String> createActivityDateToIdMap(List<IActivityDTO> activities) {
        return activities.stream().collect(Collectors.toMap(activity -> activity.getActivityDate().toString(),
                                                            IActivityDTO::getActivityId));
    }

    protected abstract List<IActivityDTO> applyPatch(List<IActivityDTO> activities, List<T> patchDataList);

    protected abstract List<String> fetchFieldsToValidate(List<T> patchDataList);
}
