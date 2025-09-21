package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import lombok.Data;

@Data
public class DeleteResourcePatchDataDTO implements IPatchDataDTO {
    private String source;
}
