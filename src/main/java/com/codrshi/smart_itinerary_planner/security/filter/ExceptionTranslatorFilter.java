package com.codrshi.smart_itinerary_planner.security.filter;

import com.codrshi.smart_itinerary_planner.util.ErrorResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@Slf4j
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
            log.error("Exception in filter chain execution: {}", ex.getMessage(), ex);
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Exception ex) throws IOException {

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }

        ErrorResponseBuilder.build(request, response, status, ex.getMessage());
    }
}
