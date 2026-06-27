package com.codrshi.smart_itinerary_planner.security.filter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties.RedisProperties.RateLimitingProperties;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.exception.TooManyRequestException;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.RequestUriIdentifier;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class RateLimiterFilter extends OncePerRequestFilter {

    private static final String RATE_LIMITING_SCRIPT = "redis/token-bucket-rate-limiting.lua";
    private static final String PROTECTED_API_PROPERTY_KEY = "protectedAPI";
    private static final String PUBLIC_API_PROPERTY_KEY = "publicAPI";

    private DefaultRedisScript<Long> redisScript;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(RATE_LIMITING_SCRIPT)));
        redisScript.setResultType(Long.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientKey;
        RateLimitingProperties rateLimitingProperties;

        if(RequestUriIdentifier.match(request.getRequestURI(), Constant.PUBLIC_APIS_THROTTLED)) {
            //TODO: proxy-trust-validation for deployment behind proxy/LB
            clientKey = Constant.RATE_LIMITING_IP_KEY_PREFIX + request.getRemoteAddr();
            rateLimitingProperties = itineraryProperties.getRedis().getRateLimiting().get(PUBLIC_API_PROPERTY_KEY);
        }
        else {
            clientKey = Constant.RATE_LIMITING_CLIENT_KEY_PREFIX + RequestContext.getUsername();
            rateLimitingProperties = itineraryProperties.getRedis().getRateLimiting().get(PROTECTED_API_PROPERTY_KEY);
        }

        Long tokens = redisTemplate.execute(redisScript, Collections.singletonList(clientKey),
                String.valueOf(rateLimitingProperties.getTokensPerPeriod()),
                String.valueOf(rateLimitingProperties.getPeriod()),
                String.valueOf(System.currentTimeMillis()));

        if (tokens == null || tokens == -1) {
            throw new TooManyRequestException();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return RequestUriIdentifier.match(request.getRequestURI(), Constant.PUBLIC_APIS_EXEMPTED);
    }
}
