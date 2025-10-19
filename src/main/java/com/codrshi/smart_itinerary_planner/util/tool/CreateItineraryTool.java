package com.codrshi.smart_itinerary_planner.util.tool;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.implementation.ValidationService;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreateItineraryTool {

    public static final String TOOL_RESPONSE = "Itinerary generation started. ItineraryId = %s";

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ICreateItineraryService createItineraryService;

    @Tool(name = "createItinerary", description = "Create an itinerary.", returnDirect = true)
    public String createItinerary(@ToolParam(description = "createItineraryRequestDTO object contains city (string), country (string) and timePeriod (ITimePeriodDTO). timePeriod field contains startDate (LocalDate) and endDate (LocalDate).")
                                      ICreateItineraryRequestDTO createItineraryRequestDTO) {

        validateRequest(createItineraryRequestDTO);
        ICreateItineraryResponseDTO responseDTO = createItineraryService.createItinerary(createItineraryRequestDTO);

        return String.format(TOOL_RESPONSE, responseDTO.getItineraryId());
    }

    private void validateRequest(ICreateItineraryRequestDTO createItineraryRequestDTO) {
        validationService.validateLocation(createItineraryRequestDTO.getCity(), "city");
        validationService.validateLocation(createItineraryRequestDTO.getCountry(), "country");

        if(createItineraryRequestDTO.getTimePeriod() == null) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_MISSING_FIELD, "timePeriod"));
        }

        LocalDate startDate = createItineraryRequestDTO.getTimePeriod().getStartDate();
        LocalDate endDate = createItineraryRequestDTO.getTimePeriod().getEndDate();

        if(startDate == null) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_MISSING_FIELD, "timePeriod.startDate"));
        }

        if(endDate == null) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_MISSING_FIELD, "timePeriod.endDate"));
        }

        if(DateUtils.isDateRangeInvalid(startDate, endDate)) {
            throw new InvalidDateRangeException(HttpStatus.BAD_REQUEST, startDate, endDate);
        }
    }
}
