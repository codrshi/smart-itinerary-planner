package com.codrshi.smart_itinerary_planner.util.patch;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UpdateNotePatchDataDTO;
import com.codrshi.smart_itinerary_planner.service.PatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.DeleteResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.UpdateNotePatchHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class PatchHandlerRegistry {
    private final Map<String, PatchHandler<?>> PATCH_HANDLER_REGISTRY = new HashMap<>();

    @Autowired
    public PatchHandlerRegistry(UpdateNotePatchHandler updateNotePatchHandler,
                             DeleteResourcePatchHandler deleteResourcePatchHandler,
                             MoveResourcePatchHandler moveResourcePatchHandler) {
        PATCH_HANDLER_REGISTRY.put(PatchOperation.UPDATE_NOTE.getValue(), updateNotePatchHandler);
        PATCH_HANDLER_REGISTRY.put(PatchOperation.DELETE_RESOURCE.getValue(), deleteResourcePatchHandler);
        PATCH_HANDLER_REGISTRY.put(PatchOperation.MOVE_RESOURCE.getValue(), moveResourcePatchHandler);
    }

    public PatchHandler<?> getHandler(String op) {
        return Optional.ofNullable(PATCH_HANDLER_REGISTRY.get(op))
                .orElseThrow(() -> new IllegalArgumentException(String.format(Constant.ERR_MSG_PATCH_OPERATION_NOT_FOUND, op)));
    }
}

