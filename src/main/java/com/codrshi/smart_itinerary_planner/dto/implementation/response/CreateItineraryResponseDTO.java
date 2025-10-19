package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.common.enums.ItineraryStatus;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "itinerary")
public class CreateItineraryResponseDTO implements ICreateItineraryResponseDTO {
    private String destination;
    private ITimePeriodDTO timePeriodDTO;
    private String itineraryId;
    private int eventsFound;
    private int attractionsFound;
}
