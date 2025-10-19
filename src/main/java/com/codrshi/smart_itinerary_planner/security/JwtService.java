package com.codrshi.smart_itinerary_planner.security;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService(@Autowired ItineraryProperties itineraryProperties){
        String envKey = itineraryProperties.getInternalApi().getApiKey();

        if (envKey == null || envKey.isBlank()) {
            throw new IllegalStateException(Constant.JWT_SECRET_KEY + " environment variable not set.");
        }

        secretKey = Keys.hmacShaKeyFor(envKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication, Instant now) {

        Principle principle = (Principle) authentication.getPrincipal();

        return Jwts.builder().subject(authentication.getName())
                .claim(Constant.USERNAME, principle.username())
                .claim(Constant.EMAIL, principle.email())
                .claim(Constant.AUTHORITIES, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .issuer(Constant.APPLICATION_NAME).issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(8, ChronoUnit.HOURS)))
                .signWith(secretKey).compact();
    }

    public Claims parseToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}
