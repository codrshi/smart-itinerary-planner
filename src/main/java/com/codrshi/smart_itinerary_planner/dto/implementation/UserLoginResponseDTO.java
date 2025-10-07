package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IUserLoginResponseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class UserLoginResponseDTO implements IUserLoginResponseDTO {
    private String username;
    private String assignedRoles;
    private String token;
    private Date tokenExpiryDate;
}
