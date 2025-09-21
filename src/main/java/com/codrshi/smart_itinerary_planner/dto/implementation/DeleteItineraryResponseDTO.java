package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class DeleteItineraryResponseDTO implements IDeleteItineraryResponseDTO {
    private List<String> itineraryIds;
    private int count;
    private boolean auditImpacted;
}
