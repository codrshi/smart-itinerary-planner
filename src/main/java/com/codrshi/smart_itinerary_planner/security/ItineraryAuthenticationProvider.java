package com.codrshi.smart_itinerary_planner.security;

import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;


public class ItineraryAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Principle principle = (Principle) authentication.getPrincipal();
        String username = principle.username();
        String password = authentication.getCredentials().toString();

        //UserDetails user = userDetailsService.loadUserByUsername(username);
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found."));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Incorrect password.");
        }

        return new UsernamePasswordAuthenticationToken(
                new Principle(username, user.getEmail()),
                password,
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getValue())).collect(Collectors.toList()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
