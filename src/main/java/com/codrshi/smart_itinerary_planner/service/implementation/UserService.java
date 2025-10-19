package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.dto.request.IUserRegistrationRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IUserLoginRequestDTO;
import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.exception.ResourceAlreadyExistException;
import com.codrshi.smart_itinerary_planner.repository.IUserRepository;
import com.codrshi.smart_itinerary_planner.security.JwtService;
import com.codrshi.smart_itinerary_planner.service.IUserService;
import com.codrshi.smart_itinerary_planner.security.Principle;
import com.codrshi.smart_itinerary_planner.util.mapper.IUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompromisedPasswordChecker compromisedPasswordChecker;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ValidationService validationService;

    @Override
    public IUserRegistrationResponseDTO createUser(IUserRegistrationRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        String email = userRequestDTO.getEmail();
        String password = userRequestDTO.getPassword();

        checkIfExistingUser(username, email);
        log.debug("No existing user found. Creating new user...");

        compromisedPasswordChecker.check(password);
        log.trace("Compromised password verification passed.");

        User user = buildUser(username, password);
        log.trace("User built successfully. Saving user...");

        User savedUser = userRepository.save(user);

        if(savedUser == null || savedUser.getDocId() == null) {
            throw new RuntimeException("Failed to create user");
        }

        log.trace("User created successfully.");
        return userMapper.mapToUserResponseDTO(savedUser);
    }

    @Override
    public IUserLoginResponseDTO authenticate(IUserLoginRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        String password = userRequestDTO.getPassword();

        UsernamePasswordAuthenticationToken authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                new Principle(username, ""), password);

        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        if(authentication==null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        log.debug("Authentication successful for user {}", username);
        log.trace("Generating JWT token...");

        Instant now = Instant.now();
        String jwtToken = jwtService.generateToken(authentication, now);

        log.trace("JWT token generated successfully.");
        return userMapper.mapToUserResponseDTO(authentication, jwtToken, Date.from(now.plus(8, ChronoUnit.HOURS)));
    }

    private User buildUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(UserRole.USER));
        user.setCreatedBy(username);
        user.setUpdatedBy(username);

        return user;
    }

    private void checkIfExistingUser(String username, String email) {
        List<User> existingUsers = userRepository.findByUsernameOrEmail(username, email);

        existingUsers.stream().filter(user -> user.getUsername().equals(username)).findAny().ifPresent(user -> {
            throw new ResourceAlreadyExistException(HttpStatus.BAD_REQUEST, "User " + username);
        });

        existingUsers.stream().filter(user -> user.getEmail().equals(email)).findAny().ifPresent(user -> {
            throw new ResourceAlreadyExistException(HttpStatus.BAD_REQUEST, "Email " + username);
        });
    }
}
