package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ActivityUtil {

    @Autowired
    private ItineraryProperties itineraryProperties;

    public int calculatePoiPerDay(List<IActivityDTO> rawActivities, int totalEvents, int totalAttractions) {

        List<Integer> slots = getAvailableSlots(rawActivities);
        int minPoiPerDay = 1;
        int maxPoiPerDay = totalEvents + totalAttractions;
        int poiPerDay = maxPoiPerDay;

        log.debug("calculating poiPerDay for slots: {}, minPoiPerDay: {}, maxPoiPerDay: {}", slots, minPoiPerDay, maxPoiPerDay);
        //TODO: Check if infinite loop
        while (minPoiPerDay < maxPoiPerDay) {
            int mid = minPoiPerDay + (maxPoiPerDay - minPoiPerDay) / 2;

            if(isSlotsFilledCompletely(slots, totalAttractions, mid)) {
                poiPerDay = mid;
                maxPoiPerDay = mid;
            }
            else  {
                minPoiPerDay = mid + 1;
            }
        }

        log.debug("poiPerDay calculated: {}", poiPerDay);
        return poiPerDay;
    }

    private List<Integer> getAvailableSlots(List<IActivityDTO> rawActivities) {
        return rawActivities.stream().filter(activity -> activity.getWeatherType().getRank() <
                        itineraryProperties.getWeatherRankThreshold())
                .map(activity -> activity.getPointOfInterests().size())
                .toList();
    }

    private boolean isSlotsFilledCompletely(List<Integer> slots, int attractionCount, int poiPerDay) {

        for(Integer slot: slots){
            if(slot >= poiPerDay) {
                continue;
            }
            attractionCount -= (poiPerDay - slot);

            if(attractionCount <= 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityId(String field) {
        return field.startsWith(Constant.ACTIVITY_ID_PREFIX) || DateUtils.isDate(field);
    }

    public static boolean isPoiId(String field) {
        return field.startsWith(Constant.POI_ID_PREFIX);
    }
}
