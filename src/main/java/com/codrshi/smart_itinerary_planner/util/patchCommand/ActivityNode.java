package com.codrshi.smart_itinerary_planner.util.patchCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityNode {
    private String activityId;
    private ActivityNode next;

    public ActivityNode(String activityId) {
        this.activityId = activityId;
        this.next = null;
    }
}
