package com.codrshi.smart_itinerary_planner.service;

import com.codrshi.smart_itinerary_planner.dto.request.IAuxiliaryRequestDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface IAuxiliaryService {
    Object query(IAuxiliaryRequestDTO auxiliaryRequestDTO);

    String mailItinerary(String itineraryId) throws IOException, MessagingException;
}
