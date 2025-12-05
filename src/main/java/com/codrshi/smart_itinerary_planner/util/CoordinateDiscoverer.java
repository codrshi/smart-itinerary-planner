package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.dto.ICoordinateDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.CoordinateDTO;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoordinateDiscoverer {
    private final String COORDINATE_FILE = "data/coordinate.csv";
    private final Map<String, ICoordinateDTO> COORDINATE_MAP = new HashMap<>();

    @PostConstruct
    public void loadCoordinates() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(COORDINATE_FILE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines().skip(1).forEach(line -> {
                String[] values = line.split(",");
                String key = getKey(values[0], values[3]);

                ICoordinateDTO coordinate = new CoordinateDTO();
                coordinate.setLatitude(Double.parseDouble(values[1]));
                coordinate.setLongitude(Double.parseDouble(values[2]));
                COORDINATE_MAP.put(key, coordinate);
            });
        }
    }

    public Optional<ICoordinateDTO> discover(String city, String countryCode) {
        return Optional.ofNullable(COORDINATE_MAP.get(getKey(city, countryCode)));
    }

    private String getKey(String city, String countryCode) {
        return (city.trim() + "|" + countryCode.trim()).toLowerCase();
    }
}





