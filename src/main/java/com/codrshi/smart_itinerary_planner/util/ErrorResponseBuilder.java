package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorResponseBuilder {

    public static void build(HttpServletRequest request, HttpServletResponse response, int status, String msg)
            throws IOException {
        Map<String, Object> links = Map.of("login", Map.of("href", linkTo(methodOn(UserController.class).login(null)).toUri().toString()));

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("message", msg);
        map.put("path", request.getRequestURI());
        map.put("traceId", RequestContext.getCurrentContext().getTraceId());
        map.put("timestamp", Instant.now().toString());
        map.put("_links", links);

        String acceptHeader = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaType.APPLICATION_JSON_VALUE);

        String body;
        String contentType;

        if (acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
            body = new XmlMapper().writeValueAsString(map);
            contentType = MediaType.APPLICATION_XML_VALUE;
        } else if (acceptHeader.contains("hal+json")) {
            body = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
            contentType = MediaTypes.HAL_JSON_VALUE;
        } else {
            body = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
            contentType = MediaType.APPLICATION_JSON_VALUE;
        }

        response.setStatus(status);
        response.setContentType(contentType);

        response.getWriter().write(body);
    }
}
