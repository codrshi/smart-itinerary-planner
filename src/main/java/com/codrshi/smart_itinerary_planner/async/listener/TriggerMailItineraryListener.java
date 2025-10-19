package com.codrshi.smart_itinerary_planner.async.listener;

import com.codrshi.smart_itinerary_planner.async.event.TriggerMailItineraryEvent;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import com.codrshi.smart_itinerary_planner.dto.ITriggerMailItineraryEventDTO;
import com.codrshi.smart_itinerary_planner.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class TriggerMailItineraryListener {

    public static final String MAIL_SUBJECT_TEMPLATE = "Smart Itinerary Planner - Summarized Itinerary for ID %s";

    @Value("classpath:ai-model/summarized-mail-template.txt")
    private Resource summarizedMailTemplate;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    private JavaMailSender javaMailSender;

    //TODO: expose events as endpoint for ADMIN role
    @EventListener
    @Async
    public void handleTriggerMailItineraryEvent(TriggerMailItineraryEvent event) {

        ITriggerMailItineraryEventDTO triggerMailItineraryEventDTO = event.getTriggerMailItineraryEventDTO();
        ITimePeriodDTO timePeriodDTO = triggerMailItineraryEventDTO.getTimePeriod();

        try {
            String subject = getSubjectContent(triggerMailItineraryEventDTO.getItineraryId());
            String body = getBodyContent(triggerMailItineraryEventDTO.getUsername(),
                                         triggerMailItineraryEventDTO.getDestination(),
                                         timePeriodDTO.getStartDate().toString(),
                                         timePeriodDTO.getEndDate().toString(),
                                         triggerMailItineraryEventDTO.getSummarizedActivities());

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromMail);
            helper.setTo(triggerMailItineraryEventDTO.getEmail());
            helper.setSubject(subject);
            helper.setText(body, false);

            javaMailSender.send(message);
        }
        catch (MessagingException ex) {
            System.err.println("MessagingException while sending email: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.err.println("IOException while sending email: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception while sending email: " + ex.getMessage());
        }
    }

    private String getSubjectContent(String... args) {
        return String.format(MAIL_SUBJECT_TEMPLATE, args);
    }

    private String getBodyContent(String... args)
            throws IOException {
        String bodyTemplate;
        try(var inputStream = summarizedMailTemplate.getInputStream()){
            bodyTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw ex;
        }

        if(bodyTemplate == null || bodyTemplate.isEmpty()) {
            throw new ResourceNotFoundException(null, "mail body template");
        }

        return String.format(bodyTemplate, args);
    }
}
