package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Instant.class})
public interface IItineraryMapper {

    @Mapping(target = "totalDays", expression = "java(DateUtils.countDays(event.getTimePeriod()))")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "activities", source = "activities")
    Itinerary mapToItineraryEntity(ICreateItineraryEventDTO event, List<IActivityDTO> activities);

    @Mapping(target = "destination", expression = "java(itinerary.getLocation().getDestination())")
    IItineraryResponseDTO mapToGetItineraryResponseDTO(Itinerary itinerary);

    @ObjectFactory
    default IItineraryResponseDTO getItineraryResponseDTO() {
        return new ItineraryResponseDTO();
    }

    default IDeleteItineraryResponseDTO mapToDeleteItineraryResponseDTO(List<String> itineraryIds, boolean auditImpacted) {
        IDeleteItineraryResponseDTO responseDTO = new DeleteItineraryResponseDTO();
        responseDTO.setItineraryIds(itineraryIds);
        responseDTO.setCount(itineraryIds.size());
        responseDTO.setAuditImpacted(auditImpacted);

        return responseDTO;
    }
}
