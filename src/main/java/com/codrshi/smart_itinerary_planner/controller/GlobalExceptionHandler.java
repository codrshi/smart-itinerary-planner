package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.IErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ErrorResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.codrshi.smart_itinerary_planner.exception.BaseException;
import com.codrshi.smart_itinerary_planner.exception.InvalidCountryException;
import com.codrshi.smart_itinerary_planner.exception.InvalidDateRangeException;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.codrshi.smart_itinerary_planner.exception.InvalidItineraryIdFormatException;
import com.codrshi.smart_itinerary_planner.exception.MissingWeatherDataException;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.common.enums.ErrorCode;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(errorMessages.toString())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request){
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(ex.getMostSpecificCause().getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidDateRangeException.class, InvalidCountryException.class, ResourceNotFoundException.class,
            MissingWeatherDataException.class, InvalidItineraryIdFormatException.class,
            InvalidEnumInstanceException.class, BadRequestException.class})
    ResponseEntity<?> handleBusinessException(BaseException ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        System.out.println(ex);
        return new ResponseEntity<>(errorResponseDTO, ex.getHttpStatus());
    }

    @ExceptionHandler(ServletException.class)
    ResponseEntity<?> handleServletException(ServletException ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("Invalid request: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        System.out.println(ex);
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CompromisedPasswordException.class)
    ResponseEntity<?> handleCompromisedPasswordException(CompromisedPasswordException ex, HttpServletRequest request) {
        IErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("The password is known to be compromised. Please use a stronger password.")
                .path(request.getRequestURI())
                .traceId(RequestContext.getCurrentContext().getTraceId())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
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

        ex.printStackTrace(new PrintStream(System.out));
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
