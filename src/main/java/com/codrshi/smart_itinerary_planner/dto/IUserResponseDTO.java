package com.codrshi.smart_itinerary_planner.dto;

import java.util.Date;

public interface IUserResponseDTO {
    String getUsername();

    void setUsername(String username);

    Date getTokenExpiryDate();

    void setTokenExpiryDate(Date tokenExpiryDate);

    String getToken();

    void setToken(String token);

    String getAssignedRoles();

    void setAssignedRoles(String assignedRoles);
}
