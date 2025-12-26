package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GeoapifyAttractionResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.TicketMasterEventResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.VirtualCrossingWeatherResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.QuotaExceededException;
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.AttractionLimitCalculator;
import com.codrshi.smart_itinerary_planner.util.FactoryUtil;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IWeatherMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalApiService implements IExternalApiService {

    private static final String KEY_TICKETMASTER = "ticketmaster";
    private static final String KEY_GEOAPIFY= "geoapify";
    public static final String KEY_VIRTUALCROSSING = "virtualCrossing";
    private static final String TICKETMASTER_GET_EVENTS = "events.json";
    private static final String GEOAPIFY_GET_ATTRACTIONS = "places";

    public static final String ERR_MSG_5XX_SERVER_ERROR = "External API server error occurred for {}. Retry will be " +
            "triggered.";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Autowired
    private IEventMapper eventMapper;

    @Autowired
    private IAttractionMapper attractionMapper;

    @Autowired
    private IWeatherMapper weatherMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Retry(name = "externalApiRetry")
    //@TimeLimiter(name = "externalApiTimeout")
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
    @CircuitBreaker(name = "externalApiCB", fallbackMethod = "getGeoapifyAttractionsFallback")
    //@TimeLimiter(name = "externalApiTimeout")
    public List<IAttractionDTO> getGeoapifyAttractions(int radius, ICoordinateDTO coordinateDTO, int totalDays) {
        final String URL = buildUrl(radius, coordinateDTO, totalDays);

        log.debug("Prepared getGeoapifyAttractions URL: {}", URL);
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        if(response.getBody().contains(Constant.OVER_QUERY_LIMIT) ) {
            log.warn("Quota exceeded for getGeoapifyAttractions. Fallback logic will be triggered.");
            throw new QuotaExceededException(LocalDate.now().toString());
        } else if(response.getStatusCode().is5xxServerError()) {
            log.warn(ERR_MSG_5XX_SERVER_ERROR, KEY_GEOAPIFY);
            throw new HttpServerErrorException(null);
        }

        GeoapifyAttractionResponseDTO attractionResponse;
        try {
            attractionResponse = objectMapper.readValue(response.getBody(), GeoapifyAttractionResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.debug("getGeoapifyAttractions response: {}", attractionResponse);
        return attractionMapper.mapToAttractionDTO(attractionResponse);
    }

    @Override
    @Retry(name = "externalApiRetry")
    @CircuitBreaker(name = "externalApiCB", fallbackMethod = "getVirtualCrossingWeatherFallback")
    //@TimeLimiter(name = "externalApiTimeout")
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

    public List<IAttractionDTO> getGeoapifyAttractionsFallback(int radius, ICoordinateDTO coordinateDTO, int totalDays, Throwable ex) {
        log.warn("getGeoapifyAttractionsFallback triggered for radius: {} and coordinateDTO: {} and totalDays: {}", radius, coordinateDTO, totalDays);
        return Collections.emptyList();
    }

    private String buildUrl(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_VIRTUALCROSSING);

        log.trace("externalApiProperty for {} key: {}", KEY_VIRTUALCROSSING, externalApiProperty);
        String coordinatePathParams = String.format("/%s,%s",coordinateDTO.getLatitude(),coordinateDTO.getLongitude());
        String timePeriodPathParams = String.format("/%s/%s",timePeriodDTO.getStartDate(),timePeriodDTO.getEndDate());

        return UriComponentsBuilder.fromUriString(externalApiProperty.getBaseUrl())
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

        return UriComponentsBuilder.fromUriString(externalApiProperty.getBaseUrl() + TICKETMASTER_GET_EVENTS)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("city", locationDTO.getCity())
                .queryParam("country", locationDTO.getCountryCode())
                .queryParam("startDateTime", startDateTime)
                .queryParam("endDateTime", endDateTime)
                .toUriString();
    }

    private String buildUrl(int radius, ICoordinateDTO coordinateDTO, int totalDays) {
        ItineraryProperties.ApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_GEOAPIFY);
        ItineraryProperties.AttractionProperties attractionProperties = itineraryProperties.getAttraction();
        int limit = AttractionLimitCalculator.calculate(totalDays, attractionProperties.getBase(),
                                                        attractionProperties.getScale(), attractionProperties.getMaxLimit());

        log.trace("externalApiProperty for {} key: {}", KEY_GEOAPIFY, externalApiProperty);
        log.trace("attractionProperties: {}", attractionProperties);

        String filter = String.format("circle:%s,%s,%s",coordinateDTO.getLongitude(), coordinateDTO.getLatitude(), radius * 1000);
        String bias = String.format("proximity:%s,%s", coordinateDTO.getLongitude(), coordinateDTO.getLatitude());

        return UriComponentsBuilder.fromUriString(externalApiProperty.getBaseUrl() + GEOAPIFY_GET_ATTRACTIONS)
                .queryParam("apiKey", externalApiProperty.getApiKey())
                .queryParam("filter", filter)
                .queryParam("bias", bias)
                .queryParam("categories", String.join(",",attractionProperties.getCategories()))
                .queryParam("limit", limit)
                .queryParam("lang", "en")
                .toUriString();
    }
}
