package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.dto.implementation.FlattenedActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.response.IItineraryResponseDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import com.codrshi.smart_itinerary_planner.service.IAuxiliaryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.IMailService;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.mapper.IFlattenedActivityMapper;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class AuxiliaryService implements IAuxiliaryService {

    public static final String MAIL_SUBJECT_TEMPLATE = "Smart Itinerary Planner - Summarized Itinerary for ID %s";

    @Value("classpath:ai-model/summarized-mail-template.txt")
    private Resource summarizedMailTemplate;

    @Autowired
    private AiModelService aiModelService;

    @Autowired
    private IGetItineraryService getItineraryService;

    @Autowired
    private IFlattenedActivityMapper flattenedItineraryMapper;

    @Autowired
    private IMailService mailService;

    @Override
    public Object query(IAuxiliaryRequestDTO auxiliaryRequestDTO) {
        return aiModelService.handleItineraryQuery(auxiliaryRequestDTO.getQuery());
    }

    @Override
    public String mailItinerary(String itineraryId) throws IOException, MessagingException {
        IItineraryResponseDTO itineraryResponseDTO = getItineraryService.getItinerary(itineraryId);

        String username = RequestContext.getCurrentContext().getUsername();
        String email = RequestContext.getCurrentContext().getEmail();
        log.debug("Username: {}, Email: {}", username, email);

        List<FlattenedActivityDTO> flattenedActivities =
                flattenedItineraryMapper.mapToFlattenedActivityList(itineraryResponseDTO.getActivities());
        log.debug("Prepared flattened activities to feed to AI model: {}", flattenedActivities);

        String summarizedActivities = aiModelService.generateItinerarySummary(flattenedActivities);

        String subject = getSubjectContent(itineraryId);
        String body = getBodyContent(username,
                                     itineraryResponseDTO.getDestination(),
                                     itineraryResponseDTO.getTimePeriod().getStartDate().toString(),
                                     itineraryResponseDTO.getTimePeriod().getEndDate().toString(),
                                     summarizedActivities);

        mailService.sendMail(subject, body);
        return body;
    }

    public static String getSubjectContent(String... args) {
        return String.format(MAIL_SUBJECT_TEMPLATE, args);
    }

    private String getBodyContent(String... args)
            throws IOException {
        String bodyTemplate;
        try(var inputStream = summarizedMailTemplate.getInputStream()){
            bodyTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("Failed to read mail body template", ex);
            throw ex;
        }

        if(bodyTemplate.isEmpty()) {
            throw new ResourceNotFoundException(null, "mail body template");
        }

        return String.format(bodyTemplate, args);
    }
}
