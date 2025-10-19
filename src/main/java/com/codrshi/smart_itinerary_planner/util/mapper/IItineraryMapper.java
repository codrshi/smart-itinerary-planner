package com.codrshi.smart_itinerary_planner.util.mapper;

import com.codrshi.smart_itinerary_planner.common.enums.WeatherType;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.ICreateItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.GetItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.PatchItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.entity.Itinerary;
import com.codrshi.smart_itinerary_planner.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Instant.class, WeatherType.class})
public interface IItineraryMapper {

    @Mapping(target = "eventsFound", source = "eventsFound")
    @Mapping(target = "attractionsFound", source = "attractionsFound")
    @Mapping(target = "destination", expression = "java(itinerary.getLocation().getDestination())")
    CreateItineraryResponseDTO mapToCreateItineraryResponseDTO(Itinerary itinerary, int eventsFound, int attractionsFound);

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
