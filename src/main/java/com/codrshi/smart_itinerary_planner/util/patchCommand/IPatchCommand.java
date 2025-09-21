package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;

public interface IPatchCommand {
    void execute(String sourceId, String targetId);
}
