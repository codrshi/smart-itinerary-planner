package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO implements IActivityDTO {
    private String activityId;
    private LocalDate activityDate;
    private WeatherType weatherType;
    @Builder.Default
    private String activityNote = "-";
    private List<IPointOfInterestDTO>  pointOfInterests;

    public ActivityDTO(IActivityDTO other) {
        this.activityId = other.getActivityId();
        this.activityDate = other.getActivityDate();
        this.weatherType = other.getWeatherType();
        this.activityNote = other.getActivityNote();
        this.pointOfInterests = other.getPointOfInterests() != null
                ? new ArrayList<>(other.getPointOfInterests())
                : null;
    }
}
