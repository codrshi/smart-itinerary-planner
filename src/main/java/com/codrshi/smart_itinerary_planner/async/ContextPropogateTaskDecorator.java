package com.codrshi.smart_itinerary_planner.async;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

public class ContextPropogateTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {

        // in scope of caller thread
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        return () -> {
            // in scope of async thread
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                SecurityContextHolder.setContext(securityContext);
                MDC.setContextMap(mdcContext);

                runnable.run();
            }
            finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
                MDC.clear();
            }
        };
    }
}
