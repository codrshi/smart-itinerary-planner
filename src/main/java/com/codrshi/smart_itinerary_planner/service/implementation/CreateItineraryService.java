package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.util.ItineraryIdGenerator;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class CreateItineraryService implements ICreateItineraryService {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private IExternalApiService externalApiService;

    @Autowired
    private IValidationService validationService;

    @Autowired
    private ConstructActivitiesService constructActivitiesService;

    @Autowired
    private LocationUtil locationUtil;

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @Override
    public ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryEventDTO) {

        ITimePeriodDTO timePeriodDTO = createItineraryEventDTO.getTimePeriod();
        ILocationDTO locationDTO = locationUtil.buildLocation(createItineraryEventDTO.getCity(), createItineraryEventDTO.getCountry());

        //TODO: handle error thrown by CompletableFuture
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

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, attractions,
                                                                                       dateToWeatherMap);

        Itinerary itinerary = constructItinerary(locationDTO, timePeriodDTO, activities);
        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        if(savedItinerary == null ||savedItinerary.getDocId() == null){
            throw new RuntimeException("Failed to persist itinerary.");
        }

        return itineraryMapper.mapToCreateItineraryResponseDTO(savedItinerary, events.size(), attractions.size());
    }

    private Itinerary constructItinerary(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO,
                                    List<IActivityDTO> activities) {
        String itineraryId = ItineraryIdGenerator.generateItineraryId(locationDTO.getCountryCode());
        String userRef = RequestContext.getCurrentContext().getUsername();

        return Itinerary.builder()
                .itineraryId(itineraryId)
                .location(locationDTO)
                .timePeriod(timePeriodDTO)
                .totalDays(DateUtils.countDays(timePeriodDTO))
                .activities(activities)
                .userRef(userRef).build();
    }
}
