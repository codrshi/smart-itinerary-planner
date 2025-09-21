package com.codrshi.smart_itinerary_planner.controller;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.service.ICreateItineraryService;
import com.codrshi.smart_itinerary_planner.service.IDeleteItineraryService;
import com.codrshi.smart_itinerary_planner.service.IGetItineraryService;
import com.codrshi.smart_itinerary_planner.service.IPatchItineraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class ControllerBaseTest {

    protected static final String URI = Constant.BASE_URI;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ICreateItineraryService createItineraryService;

    @MockitoBean
    protected IGetItineraryService getItineraryService;

    @MockitoBean
    private IDeleteItineraryService deleteItineraryService;

    @MockitoBean
    private IPatchItineraryService patchItineraryService;

    @SneakyThrows
    protected <T> T getJsonObject(String filePath, Class<T> clazz){
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            return objectMapper.readValue(is, clazz);
        }
    }
}
