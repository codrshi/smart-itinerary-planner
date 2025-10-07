package com.codrshi.smart_itinerary_planner;

import com.codrshi.smart_itinerary_planner.service.DeletePatchHandlerTest;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.DeleteResourcePatchHandler;
import com.codrshi.smart_itinerary_planner.service.implementation.patchHandler.MoveResourcePatchHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public MoveResourcePatchHandler movePatchHandler() {
        return new MoveResourcePatchHandler();
    }

    @Bean
    public DeleteResourcePatchHandler deletePatchHandler() {
        return new DeleteResourcePatchHandler();
    }
}
