package com.codrshi.smart_itinerary_planner.security.filter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.security.JwtService;
import com.codrshi.smart_itinerary_planner.security.Principle;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.RequestUriIdentifier;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(Constant.AUTH_HEADER);


        if (jwt == null || !jwt.startsWith(Constant.BEARER_TOKEN_PREFIX)) {
            // allow for public routes. AuthorizationFilter (at bottom of security chain) allows public routes
            // requests by matching authorizeHttpRequests rules
            // AuthorizationFilter throws AccessDeniedException for protected routes.
            filterChain.doFilter(request, response);
            return;
        }

        // throw BadCredentialsException is token is invalid/tampered/exprired
        try {
            jwt = jwt.substring(7);
            Claims claims = jwtService.parseToken(jwt);
            String username = claims.get(Constant.USERNAME, String.class);
            String email = claims.get(Constant.EMAIL, String.class);
            String authorities = claims.get(Constant.AUTHORITIES, String.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new Principle(username, email),
                    null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            RequestContext.setUsername(username);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Invalid or expired authentication token.");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return RequestUriIdentifier.match(request.getRequestURI(),
                                          Stream.concat(Stream.of(Constant.PUBLIC_APIS_THROTTLED),
                                                        Stream.of(Constant.PUBLIC_APIS_EXEMPTED)).toArray(String[]::new));
    }
}
