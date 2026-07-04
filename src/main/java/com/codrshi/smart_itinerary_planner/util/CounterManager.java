package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

public class CounterManager {
    private Counter activityCounter;
    private Counter poiCounter;

    public CounterManager() {
        activityCounter = new Counter(Constant.ACTIVITY_ID_PREFIX);
        poiCounter = new Counter(Constant.POI_ID_PREFIX);
    }

    public String nextActivityId() {
        return activityCounter.next();
    }

    public String nextPoiId() {
        return poiCounter.next();
    }

    public void reset() {
        activityCounter.reset();
        poiCounter.reset();
    }
}
