package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.dto.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.exception.ResourceAlreadyExistException;
import com.codrshi.smart_itinerary_planner.repository.IUserRepository;
import com.codrshi.smart_itinerary_planner.service.IUserService;
import com.codrshi.smart_itinerary_planner.util.mapper.IUserMapper;
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

@Service
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

    @Override
    public IUserResponseDTO createUser(IUserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        String password = userRequestDTO.getPassword();

        userRepository.findByUsername(username).ifPresent(user -> {
            throw new ResourceAlreadyExistException(HttpStatus.BAD_REQUEST, "User " + username);
        });

        compromisedPasswordChecker.check(password);
        User user = buildUser(username, password);

        User savedUser = userRepository.save(user);
        if(savedUser == null || savedUser.getDocId() == null) {
            throw new RuntimeException("Failed to create user");
        }

        return userMapper.mapToUserResponseDTO(savedUser);
    }

    @Override
    public IUserResponseDTO authenticate(IUserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        String password = userRequestDTO.getPassword();

        UsernamePasswordAuthenticationToken authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                password);

        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        if(authentication==null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        Instant now = Instant.now();
        String jwtToken = jwtService.generateToken(authentication, now);

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
}
