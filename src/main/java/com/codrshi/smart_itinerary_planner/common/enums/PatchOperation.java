package com.codrshi.smart_itinerary_planner.common.enums;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: handle add activity or poi patch
public enum PatchOperation {
    UPDATE_NOTE("updateNote"),
    DELETE_RESOURCE("deleteResource"),
    MOVE_RESOURCE("moveResource");

    private final String value;

    PatchOperation(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PatchOperation fromString(String value) {
        for (PatchOperation type : PatchOperation.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidEnumInstanceException(HttpStatus.BAD_REQUEST, Constant.PATCH_OPERATION, value, getValues());
    }

    private static List<String> getValues() {
        return Arrays.stream(PatchOperation.values()).map(PatchOperation::getValue).collect(Collectors.toList());
    }
}
