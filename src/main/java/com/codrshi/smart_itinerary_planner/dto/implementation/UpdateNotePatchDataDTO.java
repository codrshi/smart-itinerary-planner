package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.common.enums.PatchNoteType;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNotePatchDataDTO implements IPatchDataDTO {
    @NotNull(message = "patchData.source is null")
    private String source;
    @NotNull(message = "patchData.value is null")
    private String value;
    private PatchNoteType patchNoteType;
}
