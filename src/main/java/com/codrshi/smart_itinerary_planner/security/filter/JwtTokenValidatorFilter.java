package com.codrshi.smart_itinerary_planner.security.filter;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.security.JwtService;
import com.codrshi.smart_itinerary_planner.security.Principle;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Autowired
    public JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(Constant.AUTH_HEADER);

        try{
            if(jwt==null || !jwt.startsWith(Constant.BEARER_TOKEN_PREFIX)) {
                return;
            }

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
        }
        catch (Exception e){
            throw new BadCredentialsException("Invalid or expired authentication token.");
        }
        finally {
            filterChain.doFilter(request,response);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/user");
    }
}
