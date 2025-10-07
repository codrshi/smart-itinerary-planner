package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.dto.IUserResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.UserResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {UserRole.class, Collectors.class})
public interface IUserMapper {

    @Mapping(target = "username", source = "authentication.name")
    @Mapping(target = "assignedRoles", expression = "java(authentication.getAuthorities().stream().map(Object::toString).collect" +
            "(Collectors.joining(\", \")))")
    @Mapping(target = "token", source = "jwtToken")
    @Mapping(target = "tokenExpiryDate", source = "expiryDate")
    IUserResponseDTO mapToUserResponseDTO(Authentication authentication, String jwtToken, Date expiryDate);

    @Mapping(target = "assignedRoles", expression = "java(user.getRoles().stream().map(UserRole::getValue).collect" +
            "(Collectors.joining(\", \")))")
    IUserResponseDTO mapToUserResponseDTO(User user);

    @ObjectFactory
    default IUserResponseDTO getUserResponseDTO() {
        return new UserResponseDTO();
    }
}
