package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.Date;

@Data
@JacksonXmlRootElement(localName = "user")
public class UserLoginResponseDTO implements IUserLoginResponseDTO {
    private String username;
    private String assignedRoles;
    private String token;
    private Date tokenExpiryDate;
}
