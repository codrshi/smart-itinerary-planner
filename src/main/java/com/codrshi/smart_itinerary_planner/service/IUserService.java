package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserResponseDTO;

public interface IUserService {
    IUserResponseDTO createUser(IUserRequestDTO userRequestDTO);
    IUserResponseDTO authenticate(IUserRequestDTO userRequestDTO);
}
