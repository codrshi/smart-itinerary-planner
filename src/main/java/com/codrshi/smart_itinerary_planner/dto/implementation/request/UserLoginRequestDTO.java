package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.dto.request.IUserLoginRequestDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.Masked;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class UserLoginRequestDTO implements IUserLoginRequestDTO {
    //@NotBlank(message = "username is either null or empty")
    private String username;
    @NotBlank(message = "password is either null or empty")
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Masked
    private String password;
}
