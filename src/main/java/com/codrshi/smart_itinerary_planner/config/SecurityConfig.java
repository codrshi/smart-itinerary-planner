package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.security.filter.RateLimiterFilter;
import com.codrshi.smart_itinerary_planner.util.ErrorResponseBuilder;
import com.codrshi.smart_itinerary_planner.security.filter.ExceptionTranslatorFilter;
import com.codrshi.smart_itinerary_planner.security.ItineraryAuthenticationProvider;
import com.codrshi.smart_itinerary_planner.security.filter.JwtTokenValidatorFilter;
import com.codrshi.smart_itinerary_planner.security.filter.TraceIdHeaderFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${itinerary.security.require-ssl:false}")
    private boolean requireSsl;

    @Value("${itinerary.security.hsts-max-age-seconds:63072000}")
    private long hstsMaxAgeSeconds;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ExceptionTranslatorFilter exceptionTranslatorFilter,
                                                   JwtTokenValidatorFilter jwtTokenValidatorFilter,
                                                   TraceIdHeaderFilter traceIdHeaderFilter,
                                                   RateLimiterFilter rateLimiterFilter) throws Exception {
        if (requireSsl) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
            http.headers(headers -> headers
                    .httpStrictTransportSecurity(hsts -> hsts
                            .includeSubDomains(true)
                            .maxAgeInSeconds(hstsMaxAgeSeconds)));
        }

        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(Constant.PUBLIC_APIS).permitAll()
                .requestMatchers("/actuator/**").hasRole(UserRole.ADMIN.getName())
                .anyRequest().authenticated());

        http.addFilterBefore(traceIdHeaderFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionTranslatorFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(rateLimiterFilter, JwtTokenValidatorFilter.class);

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        ErrorResponseBuilder.build(req, res, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
                .accessDeniedHandler((req, res, e) ->
                        ErrorResponseBuilder.build(req, res, HttpServletResponse.SC_FORBIDDEN, e.getMessage()))
        );
        http.formLogin(AbstractHttpConfigurer::disable);

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
    public AuthenticationProvider authenticationProvider() {
        return new ItineraryAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtTokenValidatorFilter jwtTokenValidatorFilter() {
        return new JwtTokenValidatorFilter();
    }

    @Bean
    public ExceptionTranslatorFilter exceptionTranslatorFilter() {
        return new ExceptionTranslatorFilter();
    }

    @Bean
    public TraceIdHeaderFilter traceIdHeaderFilter() {
        return new TraceIdHeaderFilter();
    }

    @Bean
    public RateLimiterFilter rateLimiterFilter() {
        return new RateLimiterFilter();
    }
}
