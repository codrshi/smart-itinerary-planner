package com.codrshi.smart_itinerary_planner.service.implementation.patchHandler;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchNoteType;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UpdateNotePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class UpdateNotePatchHandler extends PatchHandler<UpdateNotePatchDataDTO> {
    @Override
    protected List<IActivityDTO> applyPatch(List<IActivityDTO> activities, List<UpdateNotePatchDataDTO> patchDataList) {
        final Map<String, String> ACTIVITY_DATE_TO_ID_LOOKUP = createActivityDateToIdMap(activities);
        Map<String, String> idToNoteMap = createIdToNoteMap(activities);

        log.trace("Applying update note patch with activity-date-to-id lookup: {}", ACTIVITY_DATE_TO_ID_LOOKUP);
        log.trace("Applying update note patch with id-to-note map: {}", idToNoteMap);

        patchDataList.forEach(patchData -> {
            String source = patchData.getSource();
            String incomingId = ACTIVITY_DATE_TO_ID_LOOKUP.getOrDefault(source, source);
            PatchNoteType patchNoteType = patchData.getPatchNoteType();

            if(idToNoteMap.containsKey(incomingId)) {
                String incomingNote = patchData.getValue();
                String existingNote = idToNoteMap.get(incomingId);

                if(patchNoteType == PatchNoteType.APPEND) {
                    incomingNote = Objects.equals(incomingNote,
                                                  Constant.EMPTY_NOTE) ? incomingNote: existingNote.concat(incomingNote);
                }

                idToNoteMap.put(incomingId, incomingNote);
            }

        });

        activities.forEach(activity -> {
            activity.setActivityNote(idToNoteMap.get(activity.getActivityId()));
            activity.getPointOfInterests().forEach(poi -> poi.setNote(idToNoteMap.get(poi.getPoiId())));
        });

        return activities;
    }

    @Override
    protected List<String> fetchFieldsToValidate(List<UpdateNotePatchDataDTO> patchDataList) {
        return patchDataList.stream().map(UpdateNotePatchDataDTO::getSource).toList();
    }

    private Map<String, String> createIdToNoteMap(List<IActivityDTO> activities) {
        return activities.stream()
                .flatMap(activity -> Stream.concat(
                        Stream.of(Map.entry(activity.getActivityId(),
                                            Optional.ofNullable(activity.getActivityNote()).orElse(Constant.EMPTY_NOTE))),
                        activity.getPointOfInterests().stream()
                                .map(poi -> Map.entry(poi.getPoiId(),
                                                      Optional.ofNullable(poi.getNote()).orElse(Constant.EMPTY_NOTE)))
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
