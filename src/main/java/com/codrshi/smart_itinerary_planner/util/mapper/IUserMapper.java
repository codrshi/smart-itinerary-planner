package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.dto.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        imports = {UserRole.class, Collectors.class})
public interface IUserMapper {

    @Mapping(target = "username", source = "authentication.name")
    @Mapping(target = "assignedRoles", expression = "java(authentication.getAuthorities().stream().map(Object::toString).collect" +
            "(Collectors.joining(\", \")))")
    @Mapping(target = "token", source = "jwtToken")
    @Mapping(target = "tokenExpiryDate", source = "expiryDate")
    UserLoginResponseDTO mapToUserResponseDTO(Authentication authentication, String jwtToken, Date expiryDate);

    @Mapping(target = "assignedRoles", expression = "java(user.getRoles().stream().map(UserRole::getValue).collect" +
            "(Collectors.joining(\", \")))")
    UserRegistrationResponseDTO mapToUserResponseDTO(User user);
}
