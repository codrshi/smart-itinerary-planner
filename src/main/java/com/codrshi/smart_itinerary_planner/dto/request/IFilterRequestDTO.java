package com.codrshi.smart_itinerary_planner.dto.request;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;

import java.time.LocalDate;

public interface IFilterRequestDTO {
    String getCity();

    void setCity(String city);

    String getCountry();

    void setCountry(String country);

    LocalDate getStartDate();

    void setStartDate(LocalDate startDate);

    LocalDate getEndDate();

    void setEndDate(LocalDate endDate);

    DateRangeCriteria getDateRangeCriteria();

    void setDateRangeCriteria(DateRangeCriteria dateRangeCriteria);
}
