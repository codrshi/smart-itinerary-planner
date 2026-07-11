package com.codrshi.smart_itinerary_planner.util.generator;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import com.codrshi.smart_itinerary_planner.entity.User;
import com.codrshi.smart_itinerary_planner.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    @Value("${admin.username:#{null}}")
    private String adminUsername;
    @Value("${admin.password:#{null}}")
    private String adminPassword;
    @Value("${admin.email:#{null}}")
    private String adminEmail;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (adminUsername == null || adminPassword == null || adminEmail == null) {
            log.info("Admin bootstrap skipped — admin.* properties not provided.");
            return;
        }
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            log.info("Admin user '{}' already present, skipping bootstrap.", adminUsername);
            return;
        }
        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .roles(List.of(UserRole.ADMIN))
                .build();
        userRepository.save(admin);
        log.info("Bootstrap admin user '{}' created.", adminUsername);
    }
}
