package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.controller.UserController;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.securityFilter.ExceptionTranslatorFilter;
import com.codrshi.smart_itinerary_planner.util.ItineraryAuthenticationProvider;
import com.codrshi.smart_itinerary_planner.service.implementation.ItineraryUserDetailsService;
import com.codrshi.smart_itinerary_planner.util.securityFilter.JwtTokenValidatorFilter;
import com.codrshi.smart_itinerary_planner.util.securityFilter.TraceIdHeaderFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ExceptionTranslatorFilter exceptionTranslatorFilter,
                                                   JwtTokenValidatorFilter jwtTokenValidatorFilter,
                                                   TraceIdHeaderFilter traceIdHeaderFilter) throws Exception {
        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(configurer -> configurer.disable());

        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(Constant.BASE_URI_USER + "/**").permitAll()
                .anyRequest().authenticated());

        http.addFilterBefore(traceIdHeaderFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionTranslatorFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class);

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                                                  writeError(req, res, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
                .accessDeniedHandler((req, res, e) ->
                                             writeError(req, res, HttpServletResponse.SC_FORBIDDEN, e.getMessage()))
        );
        http.formLogin(formLogin -> formLogin.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new ItineraryUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new ItineraryAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private void writeError(HttpServletRequest request, HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> links = Map.of("login", Map.of("href", linkTo(methodOn(UserController.class).login(null)).toUri().toString()));

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("message", msg);
        map.put("path", request.getRequestURI());
        map.put("traceId", RequestContext.getCurrentContext().getTraceId());
        map.put("timestamp", Instant.now().toString());
        map.put("_links", links);

        new ObjectMapper().writeValue(response.getWriter(), map);
    }
}
