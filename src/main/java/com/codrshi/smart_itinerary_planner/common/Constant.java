package com.codrshi.smart_itinerary_planner.common;

public class Constant {
    private Constant() {}

    public static final String APPLICATION_NAME = "Smart Itinerary Planner";

    // Request
    public static final String BASE_URI = "/itinerary";
    public static final String BASE_URI_ADMIN = "/itinerary/admin";
    public static final String BASE_URI_USER = "/itinerary/user";
    public static final String BASE_URI_ASSISTANT = "/itinerary/assistant";
    public static final String MAIL_ENDPOINT = "/mail/{itineraryId}";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String REGISTER_ENDPOINT = "/register";
    public static final String GET_ENDPOINT = "/{itineraryId}";
    public static final String PATCH_ENDPOINT = "/{itineraryId}";
    public static final String DELETE_ENDPOINT = "/{itineraryId}";
    public static final String PREFER_HEADER = "Prefer";
    public static final String AUTH_HEADER = "Authorization";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String PREFER_HEADER_REPRESENTATION = "return=representation";
    public static final String SYSTEM_USER = "system_user";

    // Resource
    public static final String RESOURCE_POI = "POI(s)";
    public static final String RESOURCE_ITINERARY = "itinerary";
    public static final String DATE_RANGE_CRITERIA = "dateRangeCriteria";
    public static final String PATCH_OPERATION = "patchOperation";
    public static final String PATCH_DATA = "patchData";
    public static final String PATCH_NOTE_TYPE = "patchNoteType";
    public static final String ACTIVITIES = "activities";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String AUTHORITIES = "authorities";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String VERSION = "version";

    public static final String THREAD_PREFIX = "SIP-TaskExecutor-";
    public static final String ITINERARY_ID_PREFIX = "ITIX-";
    public static final String ACTIVITY_ID_PREFIX = "ACT-";
    public static final String POI_ID_PREFIX = "POI-";
    public static final String EMPTY_NOTE = "-";
    public static final String ITINERARY_ID_REGEX = "^ITIX-[A-Z]{2,3}-.+$";
    public static final String NULL = "null";
    public static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";
    public static final String EMAIL_REDACTED = "[EMAIL_REDACTED]";
    public static final String PHONE_REDACTED = "[PHONE_REDACTED]";

    // Error message
    public static final String ERR_MSG_MISSING_CREATE_ITINERARY_EVENT = "Create itinerary event is null.";
    public static final String ERR_MSG_MISSING_ITINERARY_REQUEST = "Missing request.";
    public static final String ERR_MSG_PATCH_OPERATION_NOT_FOUND = "Patch operation not found for %s.";
    public static final String ERR_MSG_PATCH_LIMIT_EXCEED = "Patch limit of {} exceeded.";
    public static final String ERR_MSG_INVALID_PATCH_DATA = "The following field(s) for patch data are invalid: %s";
    public static final String ERR_MSG_MISSING_FIELD = "%s field is either null or empty.";
    public static final String ERR_MSG_INVALID_PATCH_DATA_STRUCTURE = "patchData has invalid structure.";
    public static final String ERR_MSG_INVALID_LOCATION = "%s contains invalid character(s).";
}
