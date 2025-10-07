package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRegistrationResponseDTO;

public interface IUserService {
    IUserRegistrationResponseDTO createUser(IUserRequestDTO userRequestDTO);
    IUserLoginResponseDTO authenticate(IUserRequestDTO userRequestDTO);
}
