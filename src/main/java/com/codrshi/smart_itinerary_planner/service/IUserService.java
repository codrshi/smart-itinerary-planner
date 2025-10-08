package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;

public interface IUserService {
    IUserRegistrationResponseDTO createUser(IUserRequestDTO userRequestDTO);
    IUserLoginResponseDTO authenticate(IUserRequestDTO userRequestDTO);
}
