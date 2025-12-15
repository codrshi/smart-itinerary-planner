package com.codrshi.smart_itinerary_planner.entity;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "itinerary")
@Getter
@Setter
@ToString
@Builder
@CompoundIndex(name = "unique_itineraryId_per_user", def = "{'itineraryId': 1, 'userRef': 1}")
public class Itinerary extends Audit{
    @Id
    private String docId;
    private String itineraryId;
    private ILocationDTO location;
    private ITimePeriodDTO timePeriod;
    private int totalDays;
    private List<IActivityDTO> activities;
    private String userRef;
}
