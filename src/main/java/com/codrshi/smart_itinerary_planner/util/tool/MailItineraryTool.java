package com.codrshi.smart_itinerary_planner.util.tool;

import com.codrshi.smart_itinerary_planner.service.IAuxiliaryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailItineraryTool {

    public static final String TOOL_RESPONSE = "Acknowledged request to send itinerary %s to your email.";

    @Autowired
    private IAuxiliaryService auxiliaryService;

    @Tool(name = "mailItinerary", description = "Send the itinerary to the user's email.", returnDirect = true)
    public String mailItinerary(@ToolParam(description = "itinerary ID (string) of the itinerary to be sent to the user's email.") String itineraryId) {
        auxiliaryService.mailItinerary(itineraryId);

        return String.format(TOOL_RESPONSE, itineraryId);
    }
}
