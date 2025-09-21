package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.IErrorResponseDTO;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
public record ErrorResponseDTO(Integer errorCode, String message, String path, String traceId, LocalDateTime timestamp)
        implements IErrorResponseDTO
{
}
