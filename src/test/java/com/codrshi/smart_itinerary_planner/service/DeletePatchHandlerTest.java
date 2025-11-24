package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.DeleteResourcePatchHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePatchHandlerTest extends BaseTest {

    @Autowired
    private DeleteResourcePatchHandler deletePatchHandler;

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenScenario1_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(2, 1);

        List<DeleteResourcePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2
        patchDataList.add(new DeleteResourcePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1));

        List<IActivityDTO> patchedActivities = deletePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/deletePatchHandler_scenario1.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities).usingRecursiveComparison().isEqualTo(expectedActivities);
    }
}
