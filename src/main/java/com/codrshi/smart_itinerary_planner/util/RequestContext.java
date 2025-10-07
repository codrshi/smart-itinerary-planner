package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class RequestContext {

    private static final ThreadLocal<RequestContext> context = ThreadLocal.withInitial(RequestContext::new);

    private String traceId;

    public static RequestContext getCurrentContext() {
        return context.get();
    }

    public static void clear() {
        context.remove();
        MDC.clear();
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
        MDC.put(Constant.TRACE_ID_HEADER, traceId);
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Constant.SYSTEM_USER;
        }

        return authentication.getName();
    }
}
