package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.async.event.CreateItineraryEvent;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserRefDTO;
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.util.ItineraryIdGenerator;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.codrshi.smart_itinerary_planner.common.enums.ItineraryStatus;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CreateItineraryService implements ICreateItineraryService {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private IExternalApiService externalApiService;

    @Autowired
    private IValidationService validationService;

    @Autowired
    private LocationUtil locationUtil;

    @Override
    public ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryEventDTO) {

        ITimePeriodDTO timePeriodDTO = createItineraryEventDTO.getTimePeriod();
        ILocationDTO locationDTO = locationUtil.buildLocation(createItineraryEventDTO.getCity(), createItineraryEventDTO.getCountry());

        ICoordinateDTO coordinateDTO = externalApiService.getOpenStreetMapCoordinate(locationDTO);
        List<IEventDTO> events = externalApiService.getTicketmasterEvents(locationDTO, timePeriodDTO);
        List<IAttractionDTO> attractions = externalApiService.getOpenStreetMapAttractions(locationDTO, coordinateDTO);
        // TODO: Itinerary generation support when weather not available
        Map<LocalDate, WeatherType> dateToWeatherMap = externalApiService.getVirtualCrossingWeather(timePeriodDTO, coordinateDTO);

        validationService.validateExternalApiResponse(events.size(), attractions.size(), dateToWeatherMap.size(),
                                                      DateUtils.countDays(timePeriodDTO));

        String itineraryId = ItineraryIdGenerator.generateItineraryId(locationDTO.getCountryCode());

        publishCreateItineraryEvent(locationDTO, timePeriodDTO, itineraryId, events, attractions, dateToWeatherMap);

        ICreateItineraryResponseDTO createItineraryResponseDTO = createResponseDTO(
                locationDTO, timePeriodDTO, itineraryId);

        return createItineraryResponseDTO;
    }

    private ICreateItineraryResponseDTO createResponseDTO(ILocationDTO locationDTO,
                                                                              ITimePeriodDTO timePeriodDTO,
                                                                              String itineraryId) {
        ICreateItineraryResponseDTO createItineraryResponseDTO = new CreateItineraryResponseDTO();
        createItineraryResponseDTO.setDestination(locationDTO.getDestination());
        createItineraryResponseDTO.setTimePeriodDTO(timePeriodDTO);
        createItineraryResponseDTO.setItineraryId(itineraryId);
        createItineraryResponseDTO.setItineraryStatus(ItineraryStatus.STARTED);
        return createItineraryResponseDTO;
    }


    private void publishCreateItineraryEvent(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO,
                                             String itineraryId, List<IEventDTO> events,
                                             List<IAttractionDTO> attractions,
                                             Map<LocalDate, WeatherType> dateToWeatherMap) {

        String userRef = RequestContext.getCurrentContext().getUsername();

        ICreateItineraryEventDTO createItineraryEventDTO =
                CreateItineraryEventDTO.builder().location(locationDTO).timePeriod(timePeriodDTO)
                        .itineraryId(itineraryId).events(events).attractions(attractions)
                        .dateToWeatherMap(dateToWeatherMap).userRef(userRef).build();

        CreateItineraryEvent createItineraryEvent = new CreateItineraryEvent(this, createItineraryEventDTO);
        publisher.publishEvent(createItineraryEvent);
    }
}
