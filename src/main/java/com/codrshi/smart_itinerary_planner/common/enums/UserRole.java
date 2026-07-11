package com.codrshi.smart_itinerary_planner.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN", "ADMIN"),
    USER("ROLE_USER", "USER");

    private final String role;
    private final String name;

    UserRole(String role, String name) {
        this.role = role;
        this.name = name;
    }
}
