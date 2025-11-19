package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.async.event.CreateResourceEvent;
import com.codrshi.smart_itinerary_planner.async.event.DeleteResourceEvent;
import com.codrshi.smart_itinerary_planner.async.event.UpdateResourceEvent;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.implementation.async.CreateResourceEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.async.DeleteResourceEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.async.UpdateResourceEventDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.CreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.DeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.response.UserRegistrationResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.ICreateItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IDeleteItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IUserRegistrationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
@Slf4j
public class ResourceEventAspect {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.controller.ItineraryController.createItinerary(..))", returning = "response")
    public void publishItineraryCreateResourceEvent(JoinPoint joinPoint, Object response) {
        ICreateItineraryResponseDTO itineraryResponseDTO = (CreateItineraryResponseDTO) getResponseContent(response);

        CreateResourceEventDTO createResourceEventDTO = CreateResourceEventDTO.builder().resourceType(Constant.RESOURCE_ITINERARY).resourceId(itineraryResponseDTO.getItineraryId()).build();
        eventPublisher.publishEvent(new CreateResourceEvent(this, createResourceEventDTO));

        log.debug("CreateResourceEvent published for itinerary resource with itineraryId = {}",
                  itineraryResponseDTO.getItineraryId());
    }

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.controller.ItineraryController.deleteItinerary(..)) && args(itineraryId)",
                    returning = "response")
    public void publishItineraryDeleteResourceEvent(JoinPoint joinPoint, String itineraryId, Object response) {

        DeleteResourceEventDTO deleteResourceEventDTO =
                DeleteResourceEventDTO.builder().resourceType(Constant.RESOURCE_ITINERARY).resourceId(itineraryId).build();
        eventPublisher.publishEvent(new DeleteResourceEvent(this, deleteResourceEventDTO));

        log.debug("DeleteResourceEvent published for itinerary resource with itineraryId = {}", itineraryId);
    }

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.controller.ItineraryController.deleteItineraries(..))",
                    returning = "response")
    public void publishItinerariesDeleteResourceEvent(JoinPoint joinPoint, Object response) {
        IDeleteItineraryResponseDTO itineraryResponseDTO = (DeleteItineraryResponseDTO) getResponseContent(response);

        itineraryResponseDTO.getItineraryIds().forEach(itineraryId -> {
            DeleteResourceEventDTO deleteResourceEventDTO =
                    DeleteResourceEventDTO.builder().resourceType(Constant.RESOURCE_ITINERARY).resourceId(itineraryId).build();
            eventPublisher.publishEvent(new DeleteResourceEvent(this, deleteResourceEventDTO));

            log.debug("DeleteResourceEvent published for itinerary resource with itineraryId = {}", itineraryId);
        });
    }

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.controller.ItineraryController.patchItinerary(..)) && args(itineraryId)",
                    returning = "response")
    public void publishItineraryUpdateResourceEvent(JoinPoint joinPoint, String itineraryId, Object response) {

        UpdateResourceEventDTO updateResourceEventDTO =
                UpdateResourceEventDTO.builder().resourceType(Constant.RESOURCE_ITINERARY).resourceId(itineraryId).build();
        eventPublisher.publishEvent(new UpdateResourceEvent(this, updateResourceEventDTO));

        log.debug("UpdateResourceEvent published for itinerary resource with itineraryId = {}", itineraryId);
    }

    @AfterReturning(value = "execution(* com.codrshi.smart_itinerary_planner.controller.UserController.createUser(..))",
                    returning = "response")
    public void publishUserCreateResourceEvent(JoinPoint joinPoint, Object response) {
        IUserRegistrationResponseDTO userResponseDTO = (UserRegistrationResponseDTO) getResponseContent(response);

        CreateResourceEventDTO createResourceEventDTO =
                CreateResourceEventDTO.builder().resourceType(Constant.RESOURCE_USER).resourceId(userResponseDTO.getUsername()).build();
        eventPublisher.publishEvent(new CreateResourceEvent(this, createResourceEventDTO));

        log.debug("CreateResourceEvent published for user resource with username = {}", userResponseDTO.getUsername());
    }

    private Object getResponseContent(Object object) {
        ResponseEntity<?> responseEntity = (ResponseEntity<?>) object;
        EntityModel<?> entityModel = (EntityModel<?>) responseEntity.getBody();

        return entityModel.getContent();
    }
}
