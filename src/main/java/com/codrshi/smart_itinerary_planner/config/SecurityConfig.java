package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.ErrorResponseBuilder;
import com.codrshi.smart_itinerary_planner.security.filter.ExceptionTranslatorFilter;
import com.codrshi.smart_itinerary_planner.security.ItineraryAuthenticationProvider;
import com.codrshi.smart_itinerary_planner.security.filter.JwtTokenValidatorFilter;
import com.codrshi.smart_itinerary_planner.security.filter.TraceIdHeaderFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
                                                  ErrorResponseBuilder.build(req, res, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
                .accessDeniedHandler((req, res, e) ->
                                             ErrorResponseBuilder.build(req, res, HttpServletResponse.SC_FORBIDDEN, e.getMessage()))
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
}
