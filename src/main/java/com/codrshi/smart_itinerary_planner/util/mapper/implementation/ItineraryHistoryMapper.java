package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRefDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserRefDTO;
import com.codrshi.smart_itinerary_planner.entity.ItineraryHistory;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryHistoryMapper;

import java.util.UUID;

public class ItineraryHistoryMapper implements IItineraryHistoryMapper {

    public ItineraryHistory map(ICreateItineraryRequestDTO createItineraryEventDTO) {
        if(createItineraryEventDTO==null)
            return null;

        String city = createItineraryEventDTO.getCity();
        String country = createItineraryEventDTO.getCountry();
        ITimePeriodDTO timePeriodDTO = createItineraryEventDTO.getTimePeriod();

        IUserRefDTO userRefDTO = new UserRefDTO();
        userRefDTO.setUserId("system_user");

        ItineraryHistory itineraryHistory = new ItineraryHistory();
        itineraryHistory.setTimePeriod(timePeriodDTO.clone());
        //itineraryHistory.setDestination(LocationUtil.buildDestination(city, country));
        itineraryHistory.setTotalDays(DateUtils.countDays(timePeriodDTO));
        itineraryHistory.setUserRef(userRefDTO);

        return itineraryHistory;
    }
}
