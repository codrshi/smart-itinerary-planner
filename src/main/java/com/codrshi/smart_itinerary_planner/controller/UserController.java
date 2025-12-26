package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.UserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserLoginResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.UserLoginRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.UserRegistrationRequestDTO;
import com.codrshi.smart_itinerary_planner.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User management", description = "Endpoints to manage users.")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "Register a new user",
               description = "Creates a new user in the database with the provided username, password and email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered a new user",
                         content = @Content(schema = @Schema(implementation = UserRegistrationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @PostMapping(Constant.REGISTER_ENDPOINT)
    //@JsonView(UserResponseView.UserDetail.class)
    public ResponseEntity<EntityModel<IUserRegistrationResponseDTO>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegistrationRequestDTO.class))
            )
            @Valid
            @RequestBody
            UserRegistrationRequestDTO userRequestDTO) {

        IUserRegistrationResponseDTO userResponseDTO = userService.createUser(userRequestDTO);

        EntityModel<IUserRegistrationResponseDTO> responseModel = EntityModel.of(userResponseDTO,
                                                                                 linkTo(methodOn(UserController.class).login(null)).withRel("login"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @Operation(summary = "Login an existing user",
               description = "Authenticates an existing user and returns a token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated the user",
                         content = @Content(schema = @Schema(implementation = UserRegistrationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))

    })
    @PostMapping(Constant.LOGIN_ENDPOINT)
    //@JsonView(UserResponseView.UserAndTokenDetail.class)
    public ResponseEntity<EntityModel<IUserLoginResponseDTO>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginRequestDTO.class))
            )
            @Valid
            @RequestBody
            UserLoginRequestDTO userRequestDTO) {

        IUserLoginResponseDTO userResponseDTO = userService.authenticate(userRequestDTO);

        EntityModel<IUserLoginResponseDTO> responseModel = EntityModel.of(userResponseDTO,
                                                                                 linkTo(methodOn(ItineraryController.class).createItinerary(null)).withRel("generate itinerary"));

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}
