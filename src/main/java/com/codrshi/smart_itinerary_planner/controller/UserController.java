package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.UserLoginRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.UserRegistrationRequestDTO;
import com.codrshi.smart_itinerary_planner.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(Constant.BASE_URI_USER)
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping(Constant.REGISTER_ENDPOINT)
    //@JsonView(UserResponseView.UserDetail.class)
    public ResponseEntity<EntityModel<IUserRegistrationResponseDTO>> createUser(@Valid @RequestBody UserRegistrationRequestDTO userRequestDTO) {

        IUserRegistrationResponseDTO userResponseDTO = userService.createUser(userRequestDTO);

        EntityModel<IUserRegistrationResponseDTO> responseModel = EntityModel.of(userResponseDTO,
                                                                                 linkTo(methodOn(UserController.class).login(null)).withRel("login"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PostMapping(Constant.LOGIN_ENDPOINT)
    //@JsonView(UserResponseView.UserAndTokenDetail.class)
    public ResponseEntity<EntityModel<IUserLoginResponseDTO>> login(@Valid @RequestBody UserLoginRequestDTO userRequestDTO) {

        IUserLoginResponseDTO userResponseDTO = userService.authenticate(userRequestDTO);

        EntityModel<IUserLoginResponseDTO> responseModel = EntityModel.of(userResponseDTO,
                                                                                 linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("generate itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}
