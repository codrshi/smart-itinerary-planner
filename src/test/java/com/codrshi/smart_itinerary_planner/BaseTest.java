package com.codrshi.smart_itinerary_planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;


@SpringBootTest
@Import(TestConfig.class)
public class BaseTest {

    @MockitoBean
    protected RestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected <T> T getJsonObject(String filePath, Class<T> clazz){
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            return objectMapper.readValue(is, clazz);
        }
    }
}
