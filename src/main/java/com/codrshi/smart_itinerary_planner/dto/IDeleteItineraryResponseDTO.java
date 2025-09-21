package com.codrshi.smart_itinerary_planner.dto;

import java.util.List;

public interface IDeleteItineraryResponseDTO {
    List<String> getItineraryIds() ;

    void setItineraryIds(List<String> itineraryIds);

    int getCount();

    void setCount(int count) ;

    boolean isAuditImpacted();

    void setAuditImpacted(boolean auditImpacted);
}
