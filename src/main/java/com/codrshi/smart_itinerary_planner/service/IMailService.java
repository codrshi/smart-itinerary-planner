package com.codrshi.smart_itinerary_planner.service;

import jakarta.mail.MessagingException;

public interface IMailService {
    void sendMail(String subject, String body)
            throws MessagingException;
}
