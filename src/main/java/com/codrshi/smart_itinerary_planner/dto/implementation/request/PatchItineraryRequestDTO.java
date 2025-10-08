package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.util.patch.PatchRequestDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(using = PatchRequestDeserializer.class)
public class PatchItineraryRequestDTO implements IPatchItineraryRequestDTO {
    @NotNull
    private PatchOperation patchOperation;
    @NotNull
    private List<IPatchDataDTO> patchData;
}
