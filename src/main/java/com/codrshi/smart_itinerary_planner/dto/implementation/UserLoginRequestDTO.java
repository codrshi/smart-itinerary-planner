package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidPassword;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO implements IUserRequestDTO {
    @NotBlank(message = "username is either null or empty")
    private String username;
    @NotBlank(message = "password is either null or empty")
    private String password;
}
