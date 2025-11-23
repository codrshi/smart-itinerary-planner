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
    public static final String PREFER_HEADER_REPRESENTATION = "return-representation";
    public static final String PREFER_HEADER_MINIMAL = "return-minimal";
    public static final String SYSTEM_USER = "system_user";

    // Resource
    public static final String RESOURCE_POI = "POI(s)";
    public static final String RESOURCE_ITINERARY = "itinerary";
    public static final String DATE_RANGE_CRITERIA = "dateRangeCriteria";
    public static final String ACTIVITY_TYPE = "activityType";
    public static final String PATCH_OPERATION = "patchOperation";
    public static final String PATCH_DATA = "patchData";
    public static final String PATCH_NOTE_TYPE = "patchNoteType";
    public static final String ACTIVITIES = "activities";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String AUTHORITIES = "authorities";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String VERSION = "version";
    public static final String RESOURCE_USER = "user";

    public static final String THREAD_PREFIX = "SIP-TaskExecutor-";
    public static final String TIME_LIMIT_SCHEDULER_PREFIX = "SIP-TimeLimitScheduler-";
    public static final String ITINERARY_ID_PREFIX = "ITIX-";
    public static final String ACTIVITY_ID_PREFIX = "ACT-";
    public static final String POI_ID_PREFIX = "POI-";
    public static final String EMPTY_NOTE = "-";
    public static final String ITINERARY_ID_REGEX = "^ITIX-[A-Z]{2,3}-.+$";
    public static final String NULL = "null";
    public static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";
    public static final String EMAIL_REDACTED = "[EMAIL_REDACTED]";
    public static final String PHONE_REDACTED = "[PHONE_REDACTED]";
    public static final String EXTERNAL_API_TIMEOUT_CONFIG = "externalApiTimeout";

    // Redis
    public static final String RATE_LIMITING_CLIENT_KEY_PREFIX = "rate_limiting_client_key:";
    public static final String ITIX_KEY_PREFIX = "itix";
    public static final String ITINERARY_KEY = "itinerary";
    public static final String ACTIVITY_KEY = "activities";
    public static final String COORDINATE_KEY = "coordinate";
    public static final String EVENT_KEY = "event";
    public static final String WEATHER_KEY = "weather";
    public static final String ATTRACTION_KEY = "attraction";
    public static final String MAILED_ITINERARIES_KEY = "mailed_itineraries";
    public static final String BLACKLISTED_MAILS_KEY = "blacklisted_mails";
    public static final String COORDINATE_CACHE = "coordinateCache";
    public static final String EVENT_CACHE = "eventCache";
    public static final String COORDINATE_KEY_GENERATOR = "coordinateRedisKeyGenerator";
    public static final String EVENT_KEY_GENERATOR = "eventRedisKeyGenerator";

    // Error message
    public static final String ERR_MSG_MISSING_ITINERARY_REQUEST = "Missing request.";
    public static final String ERR_MSG_PATCH_OPERATION_NOT_FOUND = "Patch operation not found for %s.";
    public static final String ERR_MSG_PATCH_LIMIT_EXCEED = "Patch limit of {} exceeded.";
    public static final String ERR_MSG_INVALID_PATCH_DATA = "The following field(s) for patch data are invalid: %s";
    public static final String ERR_MSG_MISSING_FIELD = "%s field is either null or empty.";
    public static final String ERR_MSG_INVALID_PATCH_DATA_STRUCTURE = "patchData has invalid structure.";
    public static final String ERR_MSG_INVALID_LOCATION = "%s contains invalid character(s).";
}
