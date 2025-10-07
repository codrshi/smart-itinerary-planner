package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.IUserResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.marker.UserResponseView;
import com.codrshi.smart_itinerary_planner.service.IUserService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.BASE_URI_USER)
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping(Constant.REGISTER_ENDPOINT)
    @JsonView(UserResponseView.UserDetail.class)
    public ResponseEntity<IUserResponseDTO> createUser(@Valid @RequestBody IUserRequestDTO userRequestDTO) {

        IUserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }

    @PostMapping(Constant.LOGIN_ENDPOINT)
    @JsonView(UserResponseView.UserAndTokenDetail.class)
    public ResponseEntity<IUserResponseDTO> login(@Valid @RequestBody IUserRequestDTO userRequestDTO) {

        IUserResponseDTO userResponseDTO = userService.authenticate(userRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }
}
