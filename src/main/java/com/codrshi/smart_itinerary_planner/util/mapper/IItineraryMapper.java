package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Instant.class})
public interface IItineraryMapper {

    @Mapping(target = "totalDays", expression = "java(DateUtils.countDays(event.getTimePeriod()))")
    //@Mapping(target = "createdAt", expression = "java(Instant.now())")
    //@Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "createdBy", expression = "java(event.getUserRef())")
    @Mapping(target = "updatedBy", expression = "java(event.getUserRef())")
    @Mapping(target = "activities", source = "activities")
    Itinerary mapToItineraryEntity(ICreateItineraryEventDTO event, List<IActivityDTO> activities);

    @Mapping(target = "destination", expression = "java(itinerary.getLocation().getDestination())")
    GetItineraryResponseDTO mapToGetItineraryResponseDTO(Itinerary itinerary);

    @Mapping(target = "activities", source = "activities")
    PatchItineraryResponseDTO mapToPatchItineraryResponseDTO(Itinerary itinerary, List<IActivityDTO> activities);

    default IDeleteItineraryResponseDTO mapToDeleteItineraryResponseDTO(List<String> itineraryIds, boolean auditImpacted) {
        IDeleteItineraryResponseDTO responseDTO = new DeleteItineraryResponseDTO();
        responseDTO.setItineraryIds(itineraryIds);
        responseDTO.setCount(itineraryIds.size());
        responseDTO.setAuditImpacted(auditImpacted);

        return responseDTO;
    }

//    @ObjectFactory
//    default IItineraryResponseDTO getItineraryResponseDTO() {
//        return new GetItineraryResponseDTO();
//    }
}
