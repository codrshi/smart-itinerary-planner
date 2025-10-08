package com.codrshi.smart_itinerary_planner.dto.response;

import java.util.Date;

public interface IUserLoginResponseDTO extends IUserRegistrationResponseDTO {
    Date getTokenExpiryDate();

    void setTokenExpiryDate(Date tokenExpiryDate);

    String getToken();

    void setToken(String token);
}
