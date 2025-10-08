package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.enums.DateRangeCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "itinerary")
@Getter
@ToString
public class ItineraryProperties {

    private int weatherRankThreshold;
    private int patchLimit;
    private PurgeProperties purge;
    private ApiProperty internalApi;
    private Map<String, ApiProperty> externalApi;
    private EndpointProperties endpoint;
    private String baseUrl;
    private CityRadiusProperties cityRadius;
    private AttractionProperties attraction;
    private AsyncProperties async;

    @ConstructorBinding
    public ItineraryProperties(int weatherRankThreshold, int patchLimit, PurgeProperties purge,
                               ApiProperty internalApi, Map<String, ApiProperty> externalApi,
                               EndpointProperties endpoint, String baseUrl, CityRadiusProperties cityRadius, AttractionProperties attraction, AsyncProperties async) {
        this.weatherRankThreshold = weatherRankThreshold;
        this.purge = purge;
        this.internalApi = internalApi;
        this.externalApi = externalApi;
        this.endpoint = endpoint;
        this.baseUrl = baseUrl;
        this.cityRadius = cityRadius;
        this.attraction = attraction;
        this.async = async;
        this.patchLimit = patchLimit;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class PurgeProperties {
        private boolean enabled;
        private DateRangeCriteria defaultCriteria;
        private PurgeCascadeProperties purgeCascade;

        @Getter
        @AllArgsConstructor
        @ToString
        public static class PurgeCascadeProperties {
            private boolean enabled;
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ApiProperty {
        private String apiKey;
        private String baseUrl;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class EndpointProperties {
        private String plan;

        @Getter
        @AllArgsConstructor
        @ToString
        public static class PlanProperties {
            private String plan;
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class CityRadiusProperties {
        private int defaultRadius;
        private Map<String,Integer> radiusMapping;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class AttractionProperties {
        private int rate;
        private int limit;
        private List<String> kinds;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class AsyncProperties {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
    }
}
