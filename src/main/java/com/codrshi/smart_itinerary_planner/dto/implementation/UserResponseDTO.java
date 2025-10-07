package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IUserResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.marker.UserResponseView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.Date;

@Data
public class UserResponseDTO implements IUserResponseDTO {
    @JsonView(UserResponseView.UserDetail.class)
    private String username;

    @JsonView(UserResponseView.UserDetail.class)
    private String assignedRoles;

    @JsonView(UserResponseView.UserAndTokenDetail.class)
    private String token;

    @JsonView(UserResponseView.UserAndTokenDetail.class)
    private Date tokenExpiryDate;
}
