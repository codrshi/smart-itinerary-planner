package com.codrshi.smart_itinerary_planner.dto.implementation.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ApiResponseWrapper<T> {
    private T response;
    private Map<String, Object> metadata;

    public ApiResponseWrapper(T response) {
        this.response = response;
        this.metadata = new HashMap<>();
    }

    public void addMetadata(String key, Object value) {
        addMetadata(key, value);
    }
}
