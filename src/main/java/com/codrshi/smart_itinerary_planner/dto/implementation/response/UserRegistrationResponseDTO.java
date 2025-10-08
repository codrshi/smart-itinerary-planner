package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "user")
public class UserRegistrationResponseDTO implements IUserRegistrationResponseDTO {
    private String username;
    private String assignedRoles;
}
