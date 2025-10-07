package com.codrshi.smart_itinerary_planner.util.securityFilter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class ExceptionTranslatorFilter extends OncePerRequestFilter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(request, response, ex);
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Exception ex) throws IOException {

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String traceId = RequestContext.getCurrentContext().getTraceId();

        if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }

        Map<String, Object> errorResponse = Map.of(
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI(),
                "traceId", traceId
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
