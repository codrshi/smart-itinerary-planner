package com.codrshi.smart_itinerary_planner.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum WeatherType {
    TYPE_0("Unknown", 0, WeatherCondition.UNKNOWN),
    TYPE_1("Blowing Or Drifting Snow", 42, WeatherCondition.HAZARDOUS),
    TYPE_2("Drizzle", 22, WeatherCondition.UNFAVOURABLE),
    TYPE_3("Heavy Drizzle", 23, WeatherCondition.UNFAVOURABLE),
    TYPE_4("Light Drizzle", 9, WeatherCondition.FINE),
    TYPE_5("Heavy Drizzle/Rain", 24, WeatherCondition.UNFAVOURABLE),
    TYPE_6("Light Drizzle/Rain", 10, WeatherCondition.FINE),
    TYPE_7("Dust storm", 41, WeatherCondition.HAZARDOUS),
    TYPE_8("Fog", 14, WeatherCondition.DISRUPTIVE),
    TYPE_9("Freezing Drizzle/Freezing Rain", 29, WeatherCondition.UNFAVOURABLE),
    TYPE_10("Heavy Freezing Drizzle/Freezing Rain", 33, WeatherCondition.UNFAVOURABLE),
    TYPE_11("Light Freezing Drizzle/Freezing Rain", 30, WeatherCondition.UNFAVOURABLE),
    TYPE_12("Freezing Fog", 32, WeatherCondition.UNFAVOURABLE),
    TYPE_13("Heavy Freezing Rain", 34, WeatherCondition.HAZARDOUS),
    TYPE_14("Light Freezing Rain", 31, WeatherCondition.UNFAVOURABLE),
    TYPE_15("Funnel Cloud/Tornado", 43, WeatherCondition.HAZARDOUS),
    TYPE_16("Hail Showers", 39, WeatherCondition.HAZARDOUS),
    TYPE_17("Ice", 28, WeatherCondition.UNFAVOURABLE),
    TYPE_18("Lightning Without Thunder", 36, WeatherCondition.HAZARDOUS),
    TYPE_19("Mist", 6, WeatherCondition.FINE),
    TYPE_20("Precipitation In Vicinity", 8, WeatherCondition.FINE),
    TYPE_21("Rain", 13, WeatherCondition.DISRUPTIVE),
    TYPE_22("Heavy Rain And Snow", 26, WeatherCondition.UNFAVOURABLE),
    TYPE_23("Light Rain And Snow", 20, WeatherCondition.DISRUPTIVE),
    TYPE_24("Rain Showers", 12, WeatherCondition.FINE),
    TYPE_25("Heavy Rain", 25, WeatherCondition.UNFAVOURABLE),
    TYPE_26("Light Rain", 11, WeatherCondition.FINE),
    TYPE_27("Sky Coverage Decreasing", 3, WeatherCondition.FAVOURABLE),
    TYPE_28("Sky Coverage Increasing", 5, WeatherCondition.FAVOURABLE),
    TYPE_29("Sky Unchanged", 4, WeatherCondition.FAVOURABLE),
    TYPE_30("Smoke Or Haze", 7, WeatherCondition.FINE),
    TYPE_31("Snow", 18, WeatherCondition.DISRUPTIVE),
    TYPE_32("Snow And Rain Showers", 19, WeatherCondition.DISRUPTIVE),
    TYPE_33("Snow Showers", 16, WeatherCondition.DISRUPTIVE),
    TYPE_34("Heavy Snow", 27, WeatherCondition.UNFAVOURABLE),
    TYPE_35("Light Snow", 17, WeatherCondition.DISRUPTIVE),
    TYPE_36("Squalls", 35, WeatherCondition.HAZARDOUS),
    TYPE_37("Thunderstorm", 38, WeatherCondition.HAZARDOUS),
    TYPE_38("Thunderstorm Without Precipitation", 37, WeatherCondition.HAZARDOUS),
    TYPE_39("Diamond Dust", 21, WeatherCondition.DISRUPTIVE),
    TYPE_40("Hail", 40, WeatherCondition.HAZARDOUS),
    TYPE_41("Overcast", 15, WeatherCondition.DISRUPTIVE),
    TYPE_42("Partially cloudy", 2, WeatherCondition.FAVOURABLE),
    TYPE_43("Clear", 1, WeatherCondition.FAVOURABLE);

    private final String label;
    private final int rank;
    private final WeatherCondition weatherCondition;

    private static final Map<String, WeatherType> LOOKUP_MAP = new HashMap<>();

    static {
        for( WeatherType weatherType : WeatherType.values() ) {
            LOOKUP_MAP.put(weatherType.label, weatherType);
        }
    }

    WeatherType(String label, int rank, WeatherCondition weatherCondition) {
        this.label = label;
        this.rank = rank;
        this.weatherCondition = weatherCondition;
    }

    public static WeatherType fromLabel(String label) {
        return LOOKUP_MAP.getOrDefault(label, WeatherType.TYPE_0);
    }

    @JsonValue
    public String weatherDescription() {
        if(getRank() == 0) {
            return getWeatherCondition().getDescription();
        }
        return String.format("%s with %s.", getWeatherCondition().getDescription(), getLabel());
    }
}
