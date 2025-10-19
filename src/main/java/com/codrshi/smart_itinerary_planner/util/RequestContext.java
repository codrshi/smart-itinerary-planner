package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.security.Principle;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        Principle principle = getPrinciple();

        return principle == null || principle.username() == null? Constant.SYSTEM_USER: principle.username();
    }

    public String getEmail() {
        Principle principle = getPrinciple();

        return principle == null || principle.email() == null? null: principle.email();
    }

    private Principle getPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return (Principle) authentication.getPrincipal();
    }

}
