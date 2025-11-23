package com.codrshi.smart_itinerary_planner.security.filter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.exception.TooManyRequestException;
import com.codrshi.smart_itinerary_planner.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class RateLimiterFilter extends OncePerRequestFilter {

    private static final String RATE_LIMITING_SCRIPT = "redis/token-bucket-rate-limiting.lua";

    private DefaultRedisScript<Long> redisScript;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtService jwtService;

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
        String clientKey = Constant.RATE_LIMITING_CLIENT_KEY_PREFIX + fetchUsernameFromJwt(request.getHeader(Constant.AUTH_HEADER));
        int tokensPerPeriod = itineraryProperties.getRedis().getRateLimiting().getTokensPerPeriod();
        int period = itineraryProperties.getRedis().getRateLimiting().getPeriod();

        Long tokens = redisTemplate.execute(redisScript, Collections.singletonList(clientKey),
                                            String.valueOf(tokensPerPeriod),
                                            String.valueOf(period),
                                            String.valueOf(System.currentTimeMillis()));

        if(tokens == -1) {
            throw new TooManyRequestException();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/user") || request.getRequestURI().contains("/actuator");
    }

    private String fetchUsernameFromJwt(String jwt) {
        jwt = jwt.substring(7);
        Claims claims = jwtService.parseToken(jwt);
        return claims.get(Constant.USERNAME, String.class);
    }
}
