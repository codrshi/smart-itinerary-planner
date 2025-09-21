package com.codrshi.smart_itinerary_planner.common.enums;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.exception.InvalidEnumInstanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PatchNoteType {
    REPLACE("replace"),
    APPEND("append");

    private final String value;

    PatchNoteType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PatchNoteType fromString(String value) {
        for (PatchNoteType type : PatchNoteType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidEnumInstanceException(HttpStatus.BAD_REQUEST, Constant.PATCH_NOTE_TYPE, value, getValues());
    }

    private static List<String> getValues() {
        return Arrays.stream(PatchNoteType.values()).map(PatchNoteType::getValue).collect(Collectors.toList());
    }
}
