package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MovePatchHandlerTest extends BaseTest {

    @Autowired
    private MoveResourcePatchHandler movePatchHandler;

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenScenario1_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(2,1);

        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 2));

        List<IActivityDTO> patchedActivities = movePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/movePatchHandler_scenario1.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities).usingRecursiveComparison().isEqualTo(expectedActivities);
    }

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenScenario2_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(3,2);

        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2, act-2 -> act-3, act-2 -> act-1
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 2));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 2,
                                                       Constant.ACTIVITY_ID_PREFIX + 3));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 2,
                                                       Constant.ACTIVITY_ID_PREFIX + 1));

        List<IActivityDTO> patchedActivities = movePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/movePatchHandler_scenario2.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities)
                .usingRecursiveComparison()
                .isEqualTo(expectedActivities);
    }

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenScenario3_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(3,2);

        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // poi-1 -> act-2, act-3 -> act-1, poi-3 -> act-3
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 2));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 3,
                                                       Constant.ACTIVITY_ID_PREFIX + 1));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 3,
                                                       Constant.ACTIVITY_ID_PREFIX + 3));

        List<IActivityDTO> patchedActivities = movePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/movePatchHandler_scenario3.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities)
                .usingRecursiveComparison()
                .isEqualTo(expectedActivities);
    }

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenMissingIds_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(1,1);

        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // poi-999 -> act-1, act-999 -> act-1, act-1 -> act-999, poi-1 -> act-999
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 999,
                                                       Constant.ACTIVITY_ID_PREFIX + 1));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 999,
                                                       Constant.ACTIVITY_ID_PREFIX + 1));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 999));
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.POI_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 999));

        List<IActivityDTO> patchedActivities = movePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/movePatchHandler_missingIds.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities)
                .usingRecursiveComparison()
                .isEqualTo(expectedActivities);
    }

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenEmptyPoi_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(2,0);
        List<MoveResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2
        patchDataList.add(new MoveResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1,
                                                       Constant.ACTIVITY_ID_PREFIX + 2));

        List<IActivityDTO> patchedActivities = movePatchHandler.handle(activities, patchDataList);

        Assertions.assertEquals(0, patchedActivities.size());
    }
}
