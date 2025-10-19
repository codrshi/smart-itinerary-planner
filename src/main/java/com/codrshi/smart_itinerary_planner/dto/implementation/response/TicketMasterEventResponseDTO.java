package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketMasterEventResponseDTO {
    private Embedded _embedded;

    @Data
    public static class Embedded {
        private List<Event> events;
    }

    @Data
    public static class Event {
        private String name;
        private Dates dates;
        private List<Classification> classifications;
        private EmbeddedVenue _embedded;
    }

    @Data
    public static class Dates {
        private Start start;
    }

    @Data
    public static class Start {
        private LocalDateTime dateTime;
    }

    @Data
    public static class Classification {
        private Category segment;
        private Category genre;
        private Category subGenre;
        private boolean family;
    }

    @Data
    public static class Category {
        private String name;
    }

    @Data
    public static class EmbeddedVenue {
        private List<Venue> venues;
    }

    @Data
    public static class Venue {
        private String name;
        private Address address;
    }

    @Data
    public static class Address {
        private String line1;
    }
}
