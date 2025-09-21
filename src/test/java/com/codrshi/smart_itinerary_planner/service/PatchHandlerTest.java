package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PatchHandlerTest extends BaseTest {

    @Autowired
    private MoveResourcePatchHandler patchHandler;

    @Autowired
    private IValidationService validationService;

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenHandle_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(3,2);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                                   .writeValueAsString(activities));

        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2, act-2 -> act-3, act-2 -> act-1, act-4 -> act-2
        // act-1 -> act-2, act-2 -> act-3, act-3 -> 1
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 2));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 2,
                                                       Constant.ACTIVITY_ID_PREFIX + 3));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 6,
                                                       Constant.ACTIVITY_ID_PREFIX + 1));
        List<IActivityDTO> patchedActivities = patchHandler.handle(activities, patchDataList);
        System.out.println("\n\n\nPatchedActivities:\n");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                                   .writeValueAsString(patchedActivities));
    }

    private List<IActivityDTO> getActivities(int totalActivities, int poiPerActivity) {
        List<IActivityDTO> activities = new ArrayList<>();
        LocalDate activityDate = LocalDate.of(2020, 1, 1);

        int[] poiCounter = {1};
        for(int actCounter = 1; actCounter <= totalActivities; actCounter++) {
            String activityId = Constant.ACTIVITY_ID_PREFIX + actCounter;
            String[] poiIds = IntStream.range(0, poiPerActivity)
                    .mapToObj(k -> Constant.POI_ID_PREFIX + (poiCounter[0] + k))
                    .toArray(String[]::new);

            activities.add(getActivity(activityId, activityDate, poiIds));
            activityDate = activityDate.plusDays(1);
            poiCounter[0] += poiPerActivity;
        }

        return activities;
    }

    private IActivityDTO getActivity(String activityId, LocalDate activityDate, String[] poiIds) {
        List<IPointOfInterestDTO> poiList = Stream.of(poiIds).map(poiId -> {
            IPointOfInterestDTO poi = new EventDTO();
            poi.setPoiId(poiId);
            return poi;
        }).collect(Collectors.toList());

        return  ActivityDTO.builder().activityId(activityId).activityDate(activityDate).pointOfInterests(poiList).build();
    }
}
