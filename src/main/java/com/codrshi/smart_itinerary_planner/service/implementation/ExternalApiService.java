package com.codrshi.smart_itinerary_planner.service.implementation;

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
import com.codrshi.smart_itinerary_planner.service.IExternalApiService;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.mapper.IAttractionMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.ICoordinateMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IEventMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IWeatherMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ExternalApiService implements IExternalApiService {

    private static final String KEY_TICKETMASTER = "ticketmaster";
    private static final String KEY_OPENSTREETMAP= "openStreetMap";
    public static final String KEY_VIRTUALCROSSING = "virtualCrossing";
    private static final String TICKETMASTER_GET_EVENTS = "events.json";
    private static final String OPENSTREETMAP_GET_COORDINATES = "en/places/geoname";
    private static final String OPENSTREETMAP_GET_ATTRACTIONS = "en/places/radius";

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
    public ICoordinateDTO getOpenStreetMapCoordinate(ILocationDTO locationDTO) {
        final String URL = buildUrl(locationDTO);

        OpenTripMapCoordinateResponseDTO openTripMapCoordinateResponseDTO = restTemplate.getForObject(URL, OpenTripMapCoordinateResponseDTO.class);

        return coordinateMapper.mapToCoordinateDTO(openTripMapCoordinateResponseDTO);

    }

    @Override
    public List<IEventDTO> getTicketmasterEvents(ILocationDTO locationDTO, ITimePeriodDTO timePeriodDTO) {
        final String URL = buildUrl(locationDTO, timePeriodDTO);

        TicketMasterEventResponseDTO ticketMasterEventResponseDTO = restTemplate.getForObject(URL, TicketMasterEventResponseDTO.class);

        return eventMapper.mapToEventDTO(ticketMasterEventResponseDTO);
    }

    @SneakyThrows
    @Override
    public List<IAttractionDTO> getOpenStreetMapAttractions(ILocationDTO locationDTO, ICoordinateDTO coordinateDTO) {
        final String URL = buildUrl(locationDTO, coordinateDTO);

        OpenTripMapAttractionResponseDTO openTripMapAttractionResponseDTO = restTemplate.getForObject(URL, OpenTripMapAttractionResponseDTO.class);

        return attractionMapper.mapToAttractionDTO(openTripMapAttractionResponseDTO);
    }

    @Override
    public Map<LocalDate, WeatherType> getVirtualCrossingWeather(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {
        final String URL = buildUrl(timePeriodDTO, coordinateDTO);

        VirtualCrossingWeatherResponseDTO virtualCrossingWeatherResponseDTO = restTemplate.getForObject(URL, VirtualCrossingWeatherResponseDTO.class);

        return weatherMapper.mapDateToWeather(virtualCrossingWeatherResponseDTO);
    }

    private String buildUrl(ITimePeriodDTO timePeriodDTO, ICoordinateDTO coordinateDTO) {
        ItineraryProperties.ExternalApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_VIRTUALCROSSING);


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
        ItineraryProperties.ExternalApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_TICKETMASTER);
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
        ItineraryProperties.ExternalApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_OPENSTREETMAP);

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl() + OPENSTREETMAP_GET_COORDINATES)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("name", locationDTO.getCity())
                .queryParam("country", locationDTO.getCountryCode())
                .toUriString();
    }

    private String buildUrl(ILocationDTO locationDTO, ICoordinateDTO coordinateDTO) {
        ItineraryProperties.ExternalApiProperty externalApiProperty =
                itineraryProperties.getExternalApi().get(KEY_OPENSTREETMAP);
        ItineraryProperties.AttractionProperties attractionProperties = itineraryProperties.getAttraction();

        return UriComponentsBuilder.fromHttpUrl(externalApiProperty.getBaseUrl() + OPENSTREETMAP_GET_ATTRACTIONS)
                .queryParam("apikey", externalApiProperty.getApiKey())
                .queryParam("lat", coordinateDTO.getLatitude())
                .queryParam("lon", coordinateDTO.getLongitude())
                .queryParam("radius", locationDTO.getRadius() * 1000)
                .queryParam("rate", attractionProperties.getRate())
                .queryParam("kinds", String.join(",",attractionProperties.getKinds()))
                .queryParam("limit", attractionProperties.getLimit())
                .toUriString();
    }
}
