package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.CreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UserRequestDTO.class)
public interface IUserRequestDTO {
    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);
}
