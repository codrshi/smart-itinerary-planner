package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.async.event.CreateItineraryEvent;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryEventDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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

    @Autowired
    private Executor taskExecutor;

    @Override
    public ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryEventDTO) {

        ITimePeriodDTO timePeriodDTO = createItineraryEventDTO.getTimePeriod();
        ILocationDTO locationDTO = locationUtil.buildLocation(createItineraryEventDTO.getCity(), createItineraryEventDTO.getCountry());

        CompletableFuture<List<IEventDTO>> eventsFuture =
                CompletableFuture.supplyAsync(() -> externalApiService.getTicketmasterEvents(locationDTO,
                                                                                             timePeriodDTO), taskExecutor);

        CompletableFuture<ICoordinateDTO> coordinateFuture = CompletableFuture.supplyAsync(
                () -> externalApiService.getOpenStreetMapCoordinate(locationDTO), taskExecutor);

        CompletableFuture<List<IAttractionDTO>> attractionsFuture =
                coordinateFuture.thenComposeAsync(coordinateDTO -> CompletableFuture.supplyAsync(
                        () -> externalApiService.getOpenStreetMapAttractions(locationDTO, coordinateDTO),
                        taskExecutor));

        CompletableFuture<Map<LocalDate, WeatherType>> weatherFuture =
                coordinateFuture.thenComposeAsync(coordinateDTO -> CompletableFuture.supplyAsync(
                        () -> externalApiService.getVirtualCrossingWeather(timePeriodDTO, coordinateDTO),
                        taskExecutor));

        CompletableFuture.allOf(eventsFuture, attractionsFuture, weatherFuture).join();

        List<IEventDTO> events = eventsFuture.join();
        List<IAttractionDTO> attractions = attractionsFuture.join();
        Map<LocalDate, WeatherType> dateToWeatherMap = weatherFuture.join();
//        ICoordinateDTO coordinateDTO = externalApiService.getOpenStreetMapCoordinate(locationDTO);
//        List<IEventDTO> events = externalApiService.getTicketmasterEvents(locationDTO, timePeriodDTO);
//        List<IAttractionDTO> attractions = externalApiService.getOpenStreetMapAttractions(locationDTO, coordinateDTO);
//
//        Map<LocalDate, WeatherType> dateToWeatherMap = externalApiService.getVirtualCrossingWeather(timePeriodDTO, coordinateDTO);

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
