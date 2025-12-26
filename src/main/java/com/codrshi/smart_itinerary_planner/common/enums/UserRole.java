package com.codrshi.smart_itinerary_planner.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
