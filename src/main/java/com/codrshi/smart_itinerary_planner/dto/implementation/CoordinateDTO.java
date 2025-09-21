package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import lombok.Data;

@Data
public class CoordinateDTO implements ICoordinateDTO {
    private double latitude;
    private double longitude;
}
