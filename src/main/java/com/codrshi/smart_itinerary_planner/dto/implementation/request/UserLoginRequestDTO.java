package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.dto.request.IUserLoginRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO implements IUserLoginRequestDTO {
    //@NotBlank(message = "username is either null or empty")
    private String username;
    @NotBlank(message = "password is either null or empty")
    private String password;
}
