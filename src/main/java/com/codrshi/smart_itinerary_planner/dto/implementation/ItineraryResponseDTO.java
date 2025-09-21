package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRefDTO;
import lombok.Data;

import java.util.List;

@Data
public class ItineraryResponseDTO implements IItineraryResponseDTO {
    private String itineraryId;
    private String destination;
    private ITimePeriodDTO timePeriod;
    private int totalDays;
    private List<IActivityDTO> activities;
    private IUserRefDTO userRef;
}
