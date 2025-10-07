package com.codrshi.smart_itinerary_planner.util.mapper.implementation;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRefDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserRefDTO;
import com.codrshi.smart_itinerary_planner.entity.ItineraryHistory;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import com.codrshi.smart_itinerary_planner.util.mapper.IItineraryHistoryMapper;

public class ItineraryHistoryMapper implements IItineraryHistoryMapper {

    public ItineraryHistory map(ICreateItineraryRequestDTO createItineraryEventDTO) {
        if(createItineraryEventDTO==null)
            return null;

        String city = createItineraryEventDTO.getCity();
        String country = createItineraryEventDTO.getCountry();
        ITimePeriodDTO timePeriodDTO = createItineraryEventDTO.getTimePeriod();

        ItineraryHistory itineraryHistory = new ItineraryHistory();
        itineraryHistory.setTimePeriod(timePeriodDTO.clone());
        //itineraryHistory.setDestination(LocationUtil.buildDestination(city, country));
        itineraryHistory.setTotalDays(DateUtils.countDays(timePeriodDTO));

        return itineraryHistory;
    }
}
