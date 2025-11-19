package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ApiResponseWrapper;
import com.codrshi.smart_itinerary_planner.dto.request.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.exception.CannotConstructActivityException;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.util.generator.ItineraryIdGenerator;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

@Service
@Slf4j
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

    @Autowired
    private TimeLimiter timeLimiter;

    @Autowired
    private ScheduledExecutorService timeLimiterScheduler;

    @Override
    public ICreateItineraryResponseDTO createItinerary(ICreateItineraryRequestDTO createItineraryRequestDTO) {

        ITimePeriodDTO timePeriodDTO = createItineraryRequestDTO.getTimePeriod();
        ILocationDTO locationDTO = locationUtil.buildLocation(createItineraryRequestDTO.getCity(), createItineraryRequestDTO.getCountry());

        log.debug("Build locationDTO: {}", locationDTO);

        CompletableFuture<List<IEventDTO>> eventsFuture = executeWithTimeLimit(
                () -> externalApiService.getTicketmasterEvents(locationDTO, timePeriodDTO));

        CompletableFuture<ICoordinateDTO> coordinateFuture = executeWithTimeLimit(
                () -> externalApiService.getOpenStreetMapCoordinate(locationDTO));

        CompletableFuture<List<IAttractionDTO>> attractionsFuture =
                coordinateFuture.thenComposeAsync(coordinateDTO ->
                                                          executeWithTimeLimit(
                                                                  () -> externalApiService.getOpenStreetMapAttractions(
                                                                          locationDTO.getRadius(),
                                                                          coordinateDTO,
                                                                          DateUtils.countDays(timePeriodDTO)
                                                                  )
                                                          )
                );

        CompletableFuture<Map<LocalDate, WeatherType>> weatherFuture =
                coordinateFuture.thenComposeAsync(coordinateDTO ->
                                                          executeWithTimeLimit(
                                                                  () -> externalApiService.getVirtualCrossingWeather(
                                                                          timePeriodDTO,
                                                                          coordinateDTO
                                                                  )
                                                          )
                );


        try {
            CompletableFuture.allOf(eventsFuture, attractionsFuture, weatherFuture).join();
        }
        catch (CompletionException ex) {
            Throwable cause = ex.getCause();
            log.error("At least one async task failed: {}", cause.toString(), cause);
            throw new RuntimeException(cause);
        }

        List<IEventDTO> events = eventsFuture.join();
        List<IAttractionDTO> attractions = attractionsFuture.join();
        Map<LocalDate, WeatherType> dateToWeatherMap = weatherFuture.join();

        log.debug("events: {}", events);
        log.debug("attractions: {}", attractions);
        log.debug("dateToWeatherMap: {}", dateToWeatherMap);
//        ICoordinateDTO coordinateDTO = externalApiService.getOpenStreetMapCoordinate(locationDTO);
//        List<IEventDTO> events = externalApiService.getTicketmasterEvents(locationDTO, timePeriodDTO);
//        List<IAttractionDTO> attractions = externalApiService.getOpenStreetMapAttractions(locationDTO, coordinateDTO);
//
//        Map<LocalDate, WeatherType> dateToWeatherMap = externalApiService.getVirtualCrossingWeather(timePeriodDTO, coordinateDTO);

        validationService.validateExternalApiResponse(events.size(), attractions.size(), dateToWeatherMap.size(),
                                                      DateUtils.countDays(timePeriodDTO));

        List<IActivityDTO> activities = constructActivities(events, attractions, dateToWeatherMap);

        Itinerary savedItinerary = persistItinerary(locationDTO, timePeriodDTO, activities);

        return itineraryMapper.mapToCreateItineraryResponseDTO(savedItinerary, events.size(), attractions.size());
    }

    private <T> CompletableFuture<T> executeWithTimeLimit(Supplier<T> supplier) {
        return timeLimiter.executeCompletionStage(
                        timeLimiterScheduler,
                        () -> CompletableFuture.supplyAsync(supplier, taskExecutor))
                .toCompletableFuture();
    }

    private List<IActivityDTO> constructActivities(List<IEventDTO> events, List<IAttractionDTO> attractions, Map<LocalDate, WeatherType> dateToWeatherMap){

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, attractions,
                                                                                       dateToWeatherMap);
        if(activities.isEmpty()) {
            throw new CannotConstructActivityException();
        }

        return activities;
    }

    private Itinerary persistItinerary(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO, List<IActivityDTO> activities) {
        Itinerary itinerary = constructItinerary(locationDTO, timePeriodDTO, activities);
        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        if(savedItinerary == null || savedItinerary.getDocId() == null){
            throw new RuntimeException("Failed to persist itinerary.");
        }

        return savedItinerary;
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
