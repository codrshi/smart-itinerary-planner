package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "itinerary")
public class GetItineraryResponseDTO implements IItineraryResponseDTO {
    private String itineraryId;
    private String destination;
    private ITimePeriodDTO timePeriod;
    private int totalDays;
    private List<IActivityDTO> activities;
    private String userRef;
}
