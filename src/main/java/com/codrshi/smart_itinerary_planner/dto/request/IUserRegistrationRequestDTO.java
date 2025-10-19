package com.codrshi.smart_itinerary_planner.dto.request;

public interface IUserRegistrationRequestDTO extends IUserLoginRequestDTO{
    String getEmail();
    void setEmail(String email);
}
