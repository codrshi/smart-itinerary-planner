package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.OpenTripMapAttractionResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.OpenTripMapCoordinateResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.TicketMasterEventResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.VirtualCrossingWeatherResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.QuotaExceededException;
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.AttractionLimitCalculator;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.ICoordinateMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IWeatherMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalApiService implements IExternalApiService {

    private static final String KEY_TICKETMASTER = "ticketmaster";
    private static final String KEY_OPENSTREETMAP= "openStreetMap";
    public static final String KEY_VIRTUALCROSSING = "virtualCrossing";
    private static final String TICKETMASTER_GET_EVENTS = "events.json";
    private static final String OPENSTREETMAP_GET_COORDINATES = "en/places/geoname";
    private static final String OPENSTREETMAP_GET_ATTRACTIONS = "en/places/radius";

    public static final String ERR_MSG_5XX_SERVER_ERROR = "External API server error occurred for %s. Retry will be " +
            "triggered.";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Autowired
    private IEventMapper eventMapper;

    @Autowired
    private ICoordinateMapper coordinateMapper;

    @Autowired
    private IAttractionMapper attractionMapper;

    @Autowired
    private IWeatherMapper weatherMapper;

    @Override
    @Retry(name = "externalApiRetry")
    @TimeLimiter(name = "externalApiTimeout")
    @Cacheable(value = Constant.COORDINATE_CACHE, keyGenerator = Constant.COORDINATE_KEY_GENERATOR)
    public ICoordinateDTO getOpenStreetMapCoordinate(ILocationDTO locationDTO) {
        final String URL = buildUrl(locationDTO);

        log.debug("Prepared getOpenStreetMapCoordinate URL: {}", URL);
        ResponseEntity<OpenTripMapCoordinateResponseDTO> response =
                restTemplate.getForEntity(URL, OpenTripMapCoordinateResponseDTO.class);

        log.debug("getOpenStreetMapCoordinate response: {}", response);
        if(response.getStatusCode().is5xxServerError()) {
            log.warn(ERR_MSG_5XX_SERVER_ERROR, KEY_OPENSTREETMAP);
            throw new HttpServerErrorException(null);
        }

        return coordinateMapper.mapToCoordinateDTO(response.getBody());
    }

    @Override
    @Retry(name = "externalApiRetry")
    @TimeLimiter(name = "externalApiTimeout")
    @Cacheable(value = Constant.EVENT_CACHE, keyGenerator = Constant.EVENT_KEY_GENERATOR)
    public List<IEventDTO> getTicketmasterEvents(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO) {
        final String URL = buildUrl(locationDTO, timePeriodDTO);

        log.debug("Prepared getTicketmasterEvents URL: {}", URL);
        ResponseEntity<TicketMasterEventResponseDTO> response = restTemplate.getForEntity(URL,
                                                                                          TicketMasterEventResponseDTO.class);

        log.debug("getTicketmasterEvents response: {}", response);
        if(response.getStatusCode().is5xxServerError()) {
            log.warn(ERR_MSG_5XX_SERVER_ERROR, KEY_TICKETMASTER);
            throw new HttpServerErrorException(null);
        }

        return eventMapper.mapToEventDTO(response.getBody());
    }

    @Override
    @Retry(name = "externalApiRetry")
    @TimeLimiter(name = "externalApiTimeout")
    public List<IAttractionDTO> getOpenStreetMapAttractions(int radius, ICoordinateDTO coordinateDTO, int totalDays) {
        final String URL = buildUrl(radius, coordinateDTO, totalDays);

        log.debug("Prepared getOpenStreetMapAttractions URL: {}", URL);
        ResponseEntity<OpenTripMapAttractionResponseDTO> response =
                restTemplate.getForEntity(URL, OpenTripMapAttractionResponseDTO.class);

        if(response.getStatusCode().is5xxServerError()) {
            log.warn(ERR_MSG_5XX_SERVER_ERROR, KEY_OPENSTREETMAP);
            throw new HttpServerErrorException(null);
        }

        log.debug("getOpenStreetMapAttractions response: {}", response);
        return attractionMapper.mapToAttractionDTO(response.getBody());
    }

    @Override
    @Retry(name = "externalApiRetry")
    @CircuitBreaker(name = "weatherApiCB", fallbackMethod = "getVirtualCrossingWeatherFallback")
    @TimeLimiter(name = "externalApiTimeout")
    public Map<LocalDate, WeatherType> getVirtualCrossingWeather(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {
        final String URL = buildUrl(timePeriodDTO, coordinateDTO);

        log.debug("Prepared getVirtualCrossingWeather URL: {}", URL);
        ResponseEntity<VirtualCrossingWeatherResponseDTO> response =
                restTemplate.getForEntity(URL, VirtualCrossingWeatherResponseDTO.class);

        if(response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            log.warn("Quota exceeded for getVirtualCrossingWeather. Fallback logic will be triggered.");
            throw new QuotaExceededException(LocalDate.now().toString());
        } else if(response.getStatusCode().is5xxServerError()) {
            log.warn(ERR_MSG_5XX_SERVER_ERROR, KEY_VIRTUALCROSSING);
            throw new HttpServerErrorException(null);
        }

        log.debug("getVirtualCrossingWeather response: {}", response);
        return weatherMapper.mapDateToWeather(response.getBody());
    }

    public Map<LocalDate, WeatherType> getVirtualCrossingWeatherFallback(ITimePeriodDTO timePeriodDTO,
                                                                         ICoordinateDTO coordinateDTO, Throwable ex) {

        log.warn("getVirtualCrossingWeatherFallback triggered for timePeriodDTO: {} and coordinateDTO: {}", timePeriodDTO, coordinateDTO);
        return FactoryUtil.defaultDateToWeatherMap(timePeriodDTO);
    }

    private String buildUrl(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_VIRTUALCROSSING);

        log.trace("externalApiProperty for {} key: {}", KEY_VIRTUALCROSSING, externalApiProperty);
        String coordinatePathParams = String.format("/%s,%s",coordinateDTO.getLatitude(),coordinateDTO.getLongitude());
        String timePeriodPathParams = String.format("/%s/%s",timePeriodDTO.getStartDate(),timePeriodDTO.getEndDate());

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl())
                .path(coordinatePathParams)
                .path(timePeriodPathParams)
                .queryParam("key", externalApiProperty.getApiKey())
                .queryParam("unitGroup", "us")
                .queryParam("include", "days")
                .queryParam("contentType","json")
                .toUriString();
    }

    private String buildUrl(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_TICKETMASTER);

        log.trace("externalApiProperty for {} key: {}", KEY_TICKETMASTER, externalApiProperty);

        String startDateTime = timePeriodDTO.getStartDate().toString() + "T00:00:00Z";
        String endDateTime = timePeriodDTO.getEndDate().toString() + "T23:59:59Z";

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl() + TICKETMASTER_GET_EVENTS)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("city", locationDTO.getCity())
                .queryParam("country", locationDTO.getCountryCode())
                .queryParam("startDateTime", startDateTime)
                .queryParam("endDateTime", endDateTime)
                .toUriString();
    }

    private String buildUrl(ILocationDTO locationDTO) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_OPENSTREETMAP);

        log.trace("externalApiProperty for {} key: {}", KEY_OPENSTREETMAP, externalApiProperty);

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl() + OPENSTREETMAP_GET_COORDINATES)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("name", locationDTO.getCity())
                .queryParam("country", locationDTO.getCountryCode())
                .toUriString();
    }

    private String buildUrl(int radius, ICoordinateDTO coordinateDTO, int totalDays) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_OPENSTREETMAP);
        ItineraryProperties.AttractionProperties attractionProperties = itineraryProperties.getAttraction();
        int limit = AttractionLimitCalculator.calculate(totalDays, attractionProperties.getBase(),
                                                        attractionProperties.getScale(), attractionProperties.getMaxLimit());

        log.trace("externalApiProperty for {} key: {}", KEY_OPENSTREETMAP, externalApiProperty);
        log.trace("attractionProperties: {}", attractionProperties);

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl() + OPENSTREETMAP_GET_ATTRACTIONS)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("lat", coordinateDTO.getLatitude())
                .queryParam("lon", coordinateDTO.getLongitude())
                .queryParam("radius", radius * 1000)
                .queryParam("rate", attractionProperties.getRate())
                .queryParam("kinds", String.join(",",attractionProperties.getKinds()))
                .queryParam("limit", limit)
                .toUriString();
    }
}
