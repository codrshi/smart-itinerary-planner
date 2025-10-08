package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import com.codrshi.smart_itinerary_planner.dto.response.IErrorResponseDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JacksonXmlRootElement(localName = "error")
public record ErrorResponseDTO(Integer errorCode, String message, String path, String traceId, LocalDateTime timestamp)
        implements IErrorResponseDTO
{
}
