package com.codrshi.smart_itinerary_planner.dto.implementation.request;

import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuxiliaryRequestDTO implements IAuxiliaryRequestDTO {
    @NotBlank(message = "query is either null or empty")
    @Size(max = 100, message = "query length exceeds 100 characters.")
    private String query;
}
