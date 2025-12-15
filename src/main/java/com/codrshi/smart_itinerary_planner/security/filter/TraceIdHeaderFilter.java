package com.codrshi.smart_itinerary_planner.security.filter;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TraceIdHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = request.getHeader(Constant.TRACE_ID_HEADER);

        if (traceId == null || traceId.isBlank()) {
            traceId = NanoIdUtils.randomNanoId();
        }

        response.setHeader(Constant.TRACE_ID_HEADER, traceId);
        RequestContext.getCurrentContext().setTraceId(traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }
}
