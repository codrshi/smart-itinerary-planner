package com.codrshi.smart_itinerary_planner.util.patch;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.MoveResourcePatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UpdateNotePatchDataDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public class PatchDataRegistry {
    private static final Map<String, Supplier<? extends IPatchDataDTO>> PATCH_DATA_REGISTRY;

    static {
        PATCH_DATA_REGISTRY = new HashMap<>();

        PATCH_DATA_REGISTRY.put(PatchOperation.UPDATE_NOTE.getValue(), UpdateNotePatchDataDTO::new);
        PATCH_DATA_REGISTRY.put(PatchOperation.DELETE_RESOURCE.getValue(), DeleteResourcePatchDataDTO::new);
        PATCH_DATA_REGISTRY.put(PatchOperation.MOVE_RESOURCE.getValue(), MoveResourcePatchDataDTO::new);
    }

    public static Class<? extends IPatchDataDTO> getClass(String patchOperation){
        return getObject(patchOperation).getClass();
    }

    public static IPatchDataDTO getObject(String patchOperation) {
        return Optional.ofNullable(
                PATCH_DATA_REGISTRY.get(patchOperation)).map(Supplier::get).orElseThrow(()->new IllegalArgumentException(String.format(Constant.ERR_MSG_PATCH_OPERATION_NOT_FOUND, patchOperation)));
    }
}
