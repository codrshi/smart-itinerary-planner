package com.codrshi.smart_itinerary_planner;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.config.AppConfig;
import com.codrshi.smart_itinerary_planner.config.AsyncConfig;
import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.dto.IActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.IPointOfInterestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.ActivityDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.EventDTO;
import com.codrshi.smart_itinerary_planner.repository.IUserRepository;
import com.codrshi.smart_itinerary_planner.repository.ItineraryRepository;
import com.codrshi.smart_itinerary_planner.security.Principle;
import com.codrshi.smart_itinerary_planner.service.IValidationService;
import com.codrshi.smart_itinerary_planner.security.JwtService;
import com.codrshi.smart_itinerary_planner.util.LocationUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest
@Import(TestConfig.class)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
public class BaseTest {

    public static final String DUMMY_EMAIL = "dummy@gmail.com";

    @Autowired
    private LocationUtil locationUtil;

    @MockitoBean
    protected RestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected IValidationService validationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private RedisCacheManager cacheManager;

    @MockitoBean
    protected Executor taskExecutor;

    @MockitoBean
    protected ItineraryRepository itineraryRepository;

    @MockitoSpyBean
    protected ItineraryProperties itineraryProperties;

    @BeforeAll
    public static void setup() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new Principle(Constant.SYSTEM_USER, DUMMY_EMAIL), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterAll
    public static void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @SneakyThrows
    protected <T> T getJsonObject(String filePath, TypeReference<T> clazz){
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            return objectMapper.readValue(is, clazz);
        }
    }

    protected List<IActivityDTO> getActivities(int totalActivities, int poiPerActivity) {
        List<IActivityDTO> activities = new ArrayList<>();
        LocalDate activityDate = LocalDate.of(2020, 1, 1);

        int[] poiCounter = {1};
        for(int actCounter = 1; actCounter <= totalActivities; actCounter++) {
            String activityId = Constant.ACTIVITY_ID_PREFIX + actCounter;
            String[] poiIds = IntStream.range(0, poiPerActivity)
                    .mapToObj(k -> Constant.POI_ID_PREFIX + (poiCounter[0] + k))
                    .toArray(String[]::new);

            activities.add(getActivity(activityId, activityDate, poiIds));
            activityDate = activityDate.plusDays(1);
            poiCounter[0] += poiPerActivity;
        }

        return activities;
    }

    private IActivityDTO getActivity(String activityId, LocalDate activityDate, String[] poiIds) {
        List<IPointOfInterestDTO> poiList = Stream.of(poiIds).map(poiId -> {
            IPointOfInterestDTO poi = new EventDTO();
            poi.setPoiId(poiId);
            return poi;
        }).collect(Collectors.toList());

        return  ActivityDTO.builder().activityId(activityId).activityDate(activityDate).pointOfInterests(poiList).build();
    }
}
