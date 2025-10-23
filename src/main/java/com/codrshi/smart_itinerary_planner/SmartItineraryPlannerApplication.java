package com.codrshi.smart_itinerary_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartItineraryPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartItineraryPlannerApplication.class, args);
	}

}
