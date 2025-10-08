package com.codrshi.smart_itinerary_planner.dto.response;

public interface IUserRegistrationResponseDTO {
    String getUsername();

    void setUsername(String username);

    String getAssignedRoles();

    void setAssignedRoles(String assignedRoles);
}
