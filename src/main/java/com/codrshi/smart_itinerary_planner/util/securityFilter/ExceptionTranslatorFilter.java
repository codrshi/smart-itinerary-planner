package com.codrshi.smart_itinerary_planner.util.securityFilter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.controller.UserController;
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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

        if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }

        Map<String, Object> links = Map.of("login", Map.of("href", linkTo(methodOn(UserController.class).login(null)).toUri().toString()));

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("message", ex.getMessage());
        map.put("path", request.getRequestURI());
        map.put("traceId", RequestContext.getCurrentContext().getTraceId());
        map.put("timestamp", Instant.now().toString());
        map.put("_links", links);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), map);
    }
}
