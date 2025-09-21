package com.codrshi.smart_itinerary_planner.dto.implementation;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.util.annotation.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@ValidDateRange
public class TimePeriodDTO implements ITimePeriodDTO, Cloneable{
    @NotNull(message = "timePeriod.startDate is null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull(message = "timePeriod.endDate is null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Override
    public ITimePeriodDTO clone() {
        ITimePeriodDTO timePeriodDTO = new TimePeriodDTO();
        timePeriodDTO.setStartDate(startDate);
        timePeriodDTO.setEndDate(endDate);

        return timePeriodDTO;
    }
}
