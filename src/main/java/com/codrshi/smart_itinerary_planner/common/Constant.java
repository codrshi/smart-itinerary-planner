package com.codrshi.smart_itinerary_planner.common;

public class Constant {
    private Constant() {}

    // Request
    public static final String BASE_URI = "/itinerary";
    public static final String GET_ENDPOINT = "/{itineraryId}";
    public static final String PATCH_ENDPOINT = "/{itineraryId}";
    public static final String DELETE_ENDPOINT = "/{itineraryId}";
    public static final String PREFER_HEADER = "Prefer";
    public static final String PREFER_HEADER_REPRESENTATION = "return=representation";

    // Resource
    public static final String RESOURCE_POI = "POI(s)";
    public static final String RESOURCE_ITINERARY = "itinerary";
    public static final String DATE_RANGE_CRITERIA = "dateRangeCriteria";
    public static final String PATCH_OPERATION = "patchOperation";
    public static final String PATCH_DATA = "patchData";
    public static final String PATCH_NOTE_TYPE = "patchNoteType";

    public static final String THREAD_PREFIX = "SIP-TaskExecutor-";
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String ITINERARY_ID_PREFIX = "ITIX-";
    public static final String ACTIVITY_ID_PREFIX = "ACT-";
    public static final String POI_ID_PREFIX = "POI-";
    public static final String EMPTY_NOTE = "-";
    public static final String ITINERARY_ID_REGEX = "^ITIX-[A-Z]{2,3}-.+$";
    public static final String NULL = "null";
    public static final String INVALID_ACTIVITY = "Invalid activity";

    // Error message
    public static final String ERR_MSG_MISSING_ITINERARY_ID = "Itinerary id is either null or empty.";
    public static final String ERR_MSG_MISSING_CREATE_ITINERARY_EVENT = "Create itinerary event is null.";
    public static final String ERR_MSG_MISSING_ITINERARY_REQUEST = "Missing request.";
    public static final String ERR_MSG_PATCH_OPERATION_NOT_FOUND = "Patch operation not found for %s.";
    public static final String ERR_MSG_PATCH_LIMIT_EXCEED = "Patch limit of {} exceeded.";
    public static final String ERR_MSG_INVALID_PATCH_DATA = "The following field(s) for patch data are invalid: %s";
    public static final String ERR_MSG_MISSING_FIELD = "%s field is either null or empty.";
    public static final String ERR_MSG_INVALID_PATCH_DATA_STRUCTURE = "patchData has invalid structure.";
}
