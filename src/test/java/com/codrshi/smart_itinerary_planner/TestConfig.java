package com.codrshi.smart_itinerary_planner;

import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public MoveResourcePatchHandler patchHandler() {
        return new MoveResourcePatchHandler();
    }
}
