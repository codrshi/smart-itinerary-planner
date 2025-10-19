package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.request.IUserRegistrationRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IUserLoginRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;

public interface IUserService {
    IUserRegistrationResponseDTO createUser(IUserRegistrationRequestDTO userRequestDTO);
    IUserLoginResponseDTO authenticate(IUserLoginRequestDTO userRequestDTO);
}
