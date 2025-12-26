package com.codrshi.smart_itinerary_planner.entity;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "itinerary_history")
@Getter
@Setter
@ToString
public class ItineraryHistory extends Audit{
    @Id
    private String docId;
    private String itineraryId;
    private String destination;
    private ITimePeriodDTO timePeriod;
    private int totalDays;
    private List<IActivityDTO> plans;
    private String userRef;
}
