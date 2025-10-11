package com.codrshi.smart_itinerary_planner.async.listener;

import com.codrshi.smart_itinerary_planner.async.event.CreateItineraryEvent;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IAttractionDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IEventDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRefDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.entity.ItineraryHistory;
import com.codrshi.smart_itinerary_planner.repository.ItineraryHistoryRepository;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.service.IConstructActivitiesService;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryHistoryMapper;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Component
public class CreateItineraryEventListener {

    @Autowired
    private IConstructActivitiesService constructActivitiesService;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private IValidationService validationService;

    @Autowired
    private IItineraryMapper itineraryMapper;

    @EventListener
    @Async
    public void handleCreateItineraryEvent(CreateItineraryEvent event) {
        // TODO: add to sync flow
        ICreateItineraryEventDTO createItineraryEventDTO = event.getCreateItineraryEventDTO();

        List<IEventDTO> events = createItineraryEventDTO.getEvents();
        List<IAttractionDTO> attractions = createItineraryEventDTO.getAttractions();
        Map<LocalDate, WeatherType> dateToWeatherMap = createItineraryEventDTO.getDateToWeatherMap();

        validationService.validateCreateItineraryEvent(createItineraryEventDTO);

        List<IActivityDTO> activities = constructActivitiesService.constructActivities(events, attractions,
                                                                                       dateToWeatherMap);

        Itinerary itinerary = itineraryMapper.mapToItineraryEntity(createItineraryEventDTO, activities);
        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        //TODO: handle error in async flow
        if(savedItinerary == null ||savedItinerary.getDocId() == null){
            throw new RuntimeException("Failed to create itinerary");
        }
    }
}
