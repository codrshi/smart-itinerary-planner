package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IFilterRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IGetItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.TimePeriodDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.exception.InvalidItineraryIdFormatException;
import com.codrshi.smart_itinerary_planner.exception.MissingWeatherDataException;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.ActivityUtil;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValidationService implements IValidationService {

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Override
    public void validateExternalApiResponse(int totalEvents, int totalAttractions, int weatherDays, int totalDays) {

        if(totalEvents + totalAttractions == 0) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, Constant.RESOURCE_POI);
        }

        if(weatherDays != totalDays) {
            throw new MissingWeatherDataException(HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void validateItineraryId(String itineraryId, HttpStatus httpStatus) {
        if(itineraryId == null || itineraryId.isBlank()) {
            throw new IllegalArgumentException(Constant.ERR_MSG_MISSING_ITINERARY_ID);
        }

        if(!itineraryId.matches(Constant.ITINERARY_ID_REGEX)) {
            throw new InvalidItineraryIdFormatException(httpStatus, Constant.ITINERARY_ID_REGEX, itineraryId);
        }
    }

    @Override
    public void validateCreateItineraryEvent(ICreateItineraryEventDTO createItineraryEventDTO) {

        if(createItineraryEventDTO == null) {
            throw new IllegalArgumentException(Constant.ERR_MSG_MISSING_CREATE_ITINERARY_EVENT);
        }

        validateItineraryId(createItineraryEventDTO.getItineraryId(), HttpStatus.INTERNAL_SERVER_ERROR);
        validateExternalApiResponse(createItineraryEventDTO.getEvents().size(), createItineraryEventDTO.getAttractions().size(), createItineraryEventDTO.getDateToWeatherMap().size(), DateUtils.countDays(createItineraryEventDTO.getTimePeriod()));
    }

    @Override
    public void validateFilterItineraryRequest(IFilterRequestDTO filterRequestDTO) {
        if(filterRequestDTO == null) {
            throw new BadRequestException(Constant.ERR_MSG_MISSING_ITINERARY_REQUEST);
        }

        LocalDate startDate = filterRequestDTO.getStartDate();
        LocalDate endDate = filterRequestDTO.getEndDate();
        DateRangeCriteria dateRangeCriteria = filterRequestDTO.getDateRangeCriteria();

        validateDateRangeCriteria(dateRangeCriteria, startDate, endDate);
        validateDateRange(startDate, endDate);
    }

    @Override
    public void validatePatchItineraryRequest(IPatchItineraryRequestDTO patchItineraryRequestDTO) {
        if(patchItineraryRequestDTO == null) {
            throw new BadRequestException(Constant.ERR_MSG_MISSING_ITINERARY_REQUEST);
        }

        List<IPatchDataDTO> patchDataList = patchItineraryRequestDTO.getPatchData();
        if(patchDataList != null && patchDataList.size() > itineraryProperties.getPatchLimit()) {
            throw new BadRequestException(Constant.ERR_MSG_PATCH_LIMIT_EXCEED);
        }
    }

    @Override
    public void validatePatchData(List<String> fields) {
        Set<String> invalidFields = new HashSet<>();

        fields.forEach(field -> {
            if(field == null){
                invalidFields.add(Constant.NULL);
            }
            else if(field.isBlank() || (!ActivityUtil.isActivityId(field) && !ActivityUtil.isPoiId(field))) {
                invalidFields.add(field);
            }
        });

        if(!invalidFields.isEmpty()) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_INVALID_PATCH_DATA, invalidFields));
        }
    }

    private void validateDateRangeCriteria(DateRangeCriteria dateRangeCriteria, LocalDate startDate, LocalDate endDate) {
        if(dateRangeCriteria != null && (startDate == null || endDate == null)) {
            throw new BadRequestException(ErrorCode.MISSING_DATE_WHEN_CRITERIA_PROVIDED);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if(DateUtils.isDateRangeInvalid(startDate, endDate)) {
            throw new InvalidDateRangeException(HttpStatus.BAD_REQUEST, startDate, endDate);
        }
    }


}
