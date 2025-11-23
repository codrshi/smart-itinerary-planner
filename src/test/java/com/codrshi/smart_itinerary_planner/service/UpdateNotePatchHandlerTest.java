package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.BaseTest;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchNoteType;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UpdateNotePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.UpdateNotePatchHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateNotePatchHandlerTest extends BaseTest {

    @Autowired
    private UpdateNotePatchHandler updatePatchHandler;

    @SneakyThrows
    @Test
    public void givenMoveResourceHandler_whenScenario1_thenReturnPatchedActivities() {
        List<IActivityDTO> activities = getActivities(2, 1);

        List<UpdateNotePatchDataDTO> patchDataList = new ArrayList<>();
        // act-1 -> act-2
        patchDataList.add(new UpdateNotePatchDataDTO(Constant.ACTIVITY_ID_PREFIX + 1,
                                                     "activity patched.", PatchNoteType.REPLACE));
        patchDataList.add(new UpdateNotePatchDataDTO(Constant.POI_ID_PREFIX + 2,
                                                     "poi patched.", null));
        patchDataList.add(new UpdateNotePatchDataDTO(Constant.POI_ID_PREFIX + 2,
                                                     "poi patched again.", PatchNoteType.APPEND));

        List<IActivityDTO> patchedActivities = updatePatchHandler.handle(activities, patchDataList);
        List<IActivityDTO> expectedActivities = getJsonObject("patchHandler/movePatchHandler_scenario1.json",
                                                              new TypeReference<>() {});

        assertThat(patchedActivities).usingRecursiveComparison().isEqualTo(expectedActivities);
    }
}
