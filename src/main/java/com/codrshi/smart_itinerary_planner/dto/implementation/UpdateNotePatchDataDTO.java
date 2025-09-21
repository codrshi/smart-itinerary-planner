package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.common.enums.PatchNoteType;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import lombok.Data;

@Data
public class UpdateNotePatchDataDTO implements IPatchDataDTO {
    private String source;
    private String value;
    private PatchNoteType patchNoteType;
}
