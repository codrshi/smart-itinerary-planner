package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;

import java.util.List;

public interface IPatchItineraryRequestDTO {
    List<IPatchDataDTO> getPatchData();

    void setPatchData(List<IPatchDataDTO> patchData);

    PatchOperation getPatchOperation();

    void setPatchOperation(PatchOperation patchOperation);
}
