package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.dto.request.IUserRegistrationRequestDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.Masked;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidPassword;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidUsername;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class UserRegistrationRequestDTO implements IUserRegistrationRequestDTO {
    @NotBlank(message = "username is either null or empty")
    @ValidUsername
    private String username;
    @NotBlank(message = "email is either null or empty")
    @Email(message = "invalid email format")
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Masked
    private String email;
    @NotBlank(message = "password is either null or empty")
    @ValidPassword
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Masked
    private String password;
}
