package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IUserRegistrationResponseDTO;
import lombok.Data;

@Data
public class UserRegistrationResponseDTO implements IUserRegistrationResponseDTO {
    private String username;
    private String assignedRoles;
}
