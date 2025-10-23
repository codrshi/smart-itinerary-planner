package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface IMailService {
    void sendMail(String subject, String body)
            throws MessagingException;
}
