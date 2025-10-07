package com.codrshi.smart_itinerary_planner.dto;

import com.codrshi.smart_itinerary_planner.dto.implementation.UserRegistrationRequestDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface IUserRequestDTO {
    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);
}
