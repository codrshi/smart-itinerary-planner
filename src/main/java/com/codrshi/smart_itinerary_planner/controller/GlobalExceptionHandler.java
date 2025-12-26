package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.dto.response.IErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.ErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.codrshi.smart_itinerary_planner.exception.BaseException;
import com.codrshi.smart_itinerary_planner.exception.CannotConstructActivityException;
import com.codrshi.smart_itinerary_planner.exception.InvalidCountryException;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.codrshi.smart_itinerary_planner.exception.InvalidItineraryIdFormatException;
import com.codrshi.smart_itinerary_planner.exception.MissingWeatherDataException;
import com.codrshi.smart_itinerary_planner.exception.ResourceAlreadyExistException;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(errorMessages.toString())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Validation error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(getContentType(request)).body(errorResponseDTO);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request){
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(ex.getMostSpecificCause().getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Http message not readable error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(getContentType(request)).body(errorResponseDTO);
    }

    @ExceptionHandler({InvalidDateRangeException.class, InvalidCountryException.class, ResourceNotFoundException.class,
            MissingWeatherDataException.class, InvalidItineraryIdFormatException.class,
            InvalidEnumInstanceException.class, BadRequestException.class, ResourceAlreadyExistException.class, CannotConstructActivityException.class})
    ResponseEntity<?> handleBusinessException(BaseException ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Business error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getHttpStatus()).contentType(getContentType(request)).body(errorResponseDTO);
    }

    @ExceptionHandler(ServletException.class)
    ResponseEntity<?> handleServletException(ServletException ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("Invalid request: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Servlet error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(getContentType(request)).body(errorResponseDTO);
    }

    @ExceptionHandler({CompromisedPasswordException.class, UsernameNotFoundException.class})
    ResponseEntity<?> authenticationException(AuthenticationException ex, HttpServletRequest request) {

        String errorMessage = ex.getMessage();
        if(ex instanceof CompromisedPasswordException) {
            errorMessage = "The password is known to be compromised. Please use a stronger password.";
        }

        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(errorMessage)
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Authentication error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(getContentType(request)).body(errorResponseDTO);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessageTemplate())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        log.error("Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(getContentType(request)).body(errorResponseDTO);
    }

    private MediaType getContentType(HttpServletRequest request) {
        String acceptHeader = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaType.APPLICATION_JSON_VALUE);

        if (acceptHeader.contains("*/*") || acceptHeader.contains("*")) {
            return MediaType.APPLICATION_JSON;
        }

        return MediaType.parseMediaType(acceptHeader);
    }
}
