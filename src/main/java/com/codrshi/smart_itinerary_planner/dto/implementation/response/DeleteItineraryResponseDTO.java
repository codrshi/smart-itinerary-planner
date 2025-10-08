package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "itinerary")
public class DeleteItineraryResponseDTO implements IDeleteItineraryResponseDTO {
    private List<String> itineraryIds;
    private int count;
    private boolean auditImpacted;
}
