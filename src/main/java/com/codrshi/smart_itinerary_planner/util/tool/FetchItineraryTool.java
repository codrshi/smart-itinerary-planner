package com.codrshi.smart_itinerary_planner.util.tool;

import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.implementation.ValidationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FetchItineraryTool {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private IGetItineraryService getItineraryService;

    @Tool(name = "fetchItinerary", description = "Fetch an itinerary.", returnDirect = true)
    public IItineraryResponseDTO fetchItinerary(@ToolParam(description = "itinerary ID (string) required to fetch itinerary")
                                  String itineraryId) {

        return getItineraryService.getItinerary(itineraryId);
    }
}
